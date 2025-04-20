package com.palangwi.soup.service;

import com.palangwi.soup.domain.Gender;
import com.palangwi.soup.domain.User;
import com.palangwi.soup.dto.user.UserInfo;
import com.palangwi.soup.dto.user.UserInitSettingResponseDto;
import com.palangwi.soup.dto.user.UserResponseDto;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.repository.UserRepository;
import com.palangwi.soup.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.palangwi.soup.domain.User.createFirstLoginUser;

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

    public void initAdditionalUserInfo(Long userId, String email, Role role, Gender gender, LocalDate birthDate) {
        User user = getUser(userId);
        user.initializeAdditionalInfo(email, role, gender, birthDate);
    }

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
}
