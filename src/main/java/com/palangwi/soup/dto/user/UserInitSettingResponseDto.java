package com.palangwi.soup.dto.user;

import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.security.Role;

import java.time.LocalDate;

public record UserInitSettingResponseDto(
        Long userId,
        String email,
        String nickname,
        Role role,
        Gender gender,
        LocalDate birthDate,
        String profileImageUrl
) {
    public static UserInitSettingResponseDto of(User user) {
        return new UserInitSettingResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getGender(),
                user.getBirthDate(),
                user.getProfileImageUrl()
        );
    }
}