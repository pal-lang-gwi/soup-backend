package com.palangwi.soup.service;

import static com.palangwi.soup.domain.user.User.createFirstLoginUser;

import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.user.UserAdditionalInfoRequestDto;
import com.palangwi.soup.dto.user.UserInitSettingResponseDto;
import com.palangwi.soup.exception.user.DuplicateNicknameException;
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
        if (userRepository.existsByUsername(userInfo.name())) {
            return getUser(userInfo.name());
        }

        User firstLoginUser = createFirstLoginUser(
                userInfo.name(),
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

        if (isNicknameDuplicate(request.nickname()) && !user.getNickname().equals(request.nickname())) {
            throw new DuplicateNicknameException();
        }

        user.updateUserInfo(request.nickname(), request.profileImageUrl());

        return UserResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void withdrawUser(Long userId) {
        User user = getUser(userId);
        user.withdraw();
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
}