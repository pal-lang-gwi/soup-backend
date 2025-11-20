package com.palangwi.soup.user.service;

import com.palangwi.soup.user.domain.Gender;
import com.palangwi.soup.user.domain.User;
import com.palangwi.soup.user.dto.*;
import com.palangwi.soup.user.exception.DuplicateNicknameException;
import com.palangwi.soup.user.exception.InvalidFormatNicknameException;
import com.palangwi.soup.user.exception.UserNotFoundException;
import com.palangwi.soup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.palangwi.soup.user.domain.User.createFirstLoginUser;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User loginOAuth(UserInfo userInfo) {
        Optional<User> userOpt = userRepository.findByEmail(userInfo.email());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            return user;
        }

        User firstLoginUser = createFirstLoginUser(
                userInfo.email(),
                userInfo.name(),
                userInfo.providerId()
        );

        userRepository.save(firstLoginUser);

        return firstLoginUser;
    }

    public UserInitSettingResponseDto initAdditionalUserInfo(Long userId, UserAdditionalInfoRequestDto request) {
        User user = getUser(userId);
        Gender gender = Gender.valueOf(request.gender().toUpperCase());

        if(userRepository.existsByNickname(request.nickname())) {
            throw new DuplicateNicknameException();
        }

        user.initializeAdditionalInfo(request.nickname(), gender, request.birthDate());

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

    public void deleteAccount(Long userId) {
        LocalDateTime now =  LocalDateTime.now();
        User user = getUser(userId);

        user.deleteUser(now);
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
        return userRepository.findByUsername(username)
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