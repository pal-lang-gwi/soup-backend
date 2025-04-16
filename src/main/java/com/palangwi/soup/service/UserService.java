package com.palangwi.soup.service;

import com.palangwi.soup.domain.User;
import com.palangwi.soup.dto.UserInfo;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.repository.UserRepository;
import com.palangwi.soup.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                userInfo.email(),
                userInfo.name(),
                userInfo.nickname(),
                Role.USER,
                userInfo.gender(),
                userInfo.birthDate(),
                userInfo.providerId(),
                userInfo.profileImageUrl()
        );

        userRepository.save(firstLoginUser);

        return firstLoginUser;
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
