package com.palangwi.soup.service.user;

import static com.palangwi.soup.domain.user.User.createFirstLoginUser;

import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.userlog.UserHistory;
import com.palangwi.soup.dto.UserInfo;
import com.palangwi.soup.dto.user.*;
import com.palangwi.soup.exception.user.DuplicateNicknameException;
import com.palangwi.soup.exception.user.InvalidFormatNicknameException;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.repository.UserHistoryRepository;
import com.palangwi.soup.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserHistoryService userHistoryService;

    public User loginOAuth(UserInfo userInfo) {
        Optional<User> userOpt = userRepository.findByEmail(userInfo.email());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            return user;
        }

        User firstLoginUser = createFirstLoginUser(
                userInfo.email(),
                userInfo.nickname(),
                userInfo.providerId()
        );

        userRepository.save(firstLoginUser);

        return firstLoginUser;
    }

    public UserInitSettingResponseDto initAdditionalUserInfo(Long userId, UserAdditionalInfoRequestDto request) {
        User user = getUser(userId);
        Gender gender = Gender.valueOf(request.gender().toUpperCase());

        user.initializeAdditionalInfo(request.email(), gender, request.birthDate());

        userHistoryService.saveCreateHistory(user);

        return UserInitSettingResponseDto.of(user);
    }

    public UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto request) {
        User user = getUser(userId);

        validateDuplicateNickname(request, user);

        user.updateUserInfo(request.nickname(), request.profileImageUrl());

        return UserResponseDto.of(user);
    }

    private void validateDuplicateNickname(UserUpdateRequestDto request, User user) {
        if (isNicknameDuplicate(request.nickname()) && !user.getNickname().equals(request.nickname())) {
            throw new DuplicateNicknameException();
        }
    }

    @Transactional(readOnly = true)
    public boolean isAvailableNickname(String nickname) {
        validateNicknameFormat(nickname);
        return !isNicknameDuplicate(nickname);
    }

    public void deleteAccount(Long userId, UserDeleteRequestDto request) {
        User user = getUser(userId);

        userHistoryService.saveDeleteHistory(user, request);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(Long userId) {
        User user = getUser(userId);

        return UserResponseDto.of(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private User getUser(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validateNicknameFormat(String nickname) {
        if (isNullOrEmpty(nickname) || isInvalidLength(nickname) || isInvalidPattern(nickname)) {
            throw new InvalidFormatNicknameException();
        }
    }

    private boolean isNullOrEmpty(String nickname) {
        return nickname == null;
    }

    private boolean isInvalidLength(String nickname) {
        int length = nickname.length();
        return length < 2 || length > 10;
    }

    private boolean isInvalidPattern(String nickname) {
        return !nickname.matches("^[가-힣a-zA-Z0-9]+$");
    }

    private boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}