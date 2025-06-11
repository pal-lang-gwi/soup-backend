package com.palangwi.soup.dto.admin.keyword;

import com.palangwi.soup.domain.user.User;

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
