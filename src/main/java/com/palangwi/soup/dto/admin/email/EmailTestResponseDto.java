package com.palangwi.soup.dto.admin.email;

import com.palangwi.soup.domain.user.User;

public record EmailTestResponseDto(String email) {
    public static EmailTestResponseDto of(User user) {
        return new EmailTestResponseDto(user.getEmail());
    }
}
