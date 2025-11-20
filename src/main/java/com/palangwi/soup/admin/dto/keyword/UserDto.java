package com.palangwi.soup.admin.dto.keyword;

import com.palangwi.soup.user.domain.User;

public record UserDto(
        Long userId,
        String email
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail()
        );
    }
}
