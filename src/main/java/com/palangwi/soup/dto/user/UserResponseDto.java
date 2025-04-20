package com.palangwi.soup.dto.user;

import com.palangwi.soup.domain.User;

public record UserResponseDto(String nickname,
                              String profileImageUrl) {

    public static UserResponseDto of(User user) {
        return new UserResponseDto(
                user.getNickname(),
                user.getProfileImageUrl()
        );
    }
}