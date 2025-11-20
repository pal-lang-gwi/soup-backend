package com.palangwi.soup.user.dto;

import com.palangwi.soup.common.security.Role;
import com.palangwi.soup.user.domain.Gender;
import com.palangwi.soup.user.domain.User;
import lombok.Builder;

import java.time.LocalDate;

@Builder
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