package com.palangwi.soup.dto.user;

import com.palangwi.soup.domain.User;
import com.palangwi.soup.domain.Gender;
import com.palangwi.soup.security.Role;

import java.time.LocalDate;

public record UserInitSettingResponseDto(
        Long userId,
        String email,
        String nickname,
        String profileImageUrl,
        Role role,
        Gender gender,
        LocalDate birthDate
) {
    public static UserInitSettingResponseDto of(User user) {
        return new UserInitSettingResponseDto(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getRole(),
                user.getGender(),
                user.getBirthDate(),
                user.getEmail()
        );
    }
}