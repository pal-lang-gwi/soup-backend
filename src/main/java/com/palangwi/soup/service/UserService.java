package com.palangwi.soup.service;

import static com.palangwi.soup.domain.user.User.createFirstLoginUser;

import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.UserInfo;
import com.palangwi.soup.dto.user.UserAdditionalInfoRequestDto;
import com.palangwi.soup.dto.user.UserInitSettingResponseDto;
import com.palangwi.soup.dto.user.UserResponseDto;
import com.palangwi.soup.dto.user.UserUpdateRequestDto;
import com.palangwi.soup.exception.user.DuplicateNicknameException;
import com.palangwi.soup.exception.user.InvalidFormatNicknameException;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User loginOAuth(UserInfo userInfo) {
        if (userRepository.existsByUsername(userInfo.username())) {
            return getUser(userInfo.username());
        }

        User firstLoginUser = createFirstLoginUser(
                userInfo.username(),
                userInfo.nickname(),
                userInfo.providerId()
        );

        userRepository.save(firstLoginUser);

        return firstLoginUser;
    }

    public UserInitSettingResponseDto initAdditionalUserInfo(Long userId, UserAdditionalInfoRequestDto request) {
        User user = getUser(userId);
        user.initializeAdditionalInfo(request.email(), request.role(), request.gender(), request.birthDate());

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
        User user = getUser(userId);
        user.softDelete();
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
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(UserNotFoundException::new);
    }

    private void validateNicknameFormat(String nickname) {
        boolean isInvalid = nickname == null
                || nickname.length() < 2
                || nickname.length() > 10
                || !nickname.matches("^[가-힣a-zA-Z0-9]+$");

        if (isInvalid) {
            throw new InvalidFormatNicknameException();
        }
    }

    private boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}