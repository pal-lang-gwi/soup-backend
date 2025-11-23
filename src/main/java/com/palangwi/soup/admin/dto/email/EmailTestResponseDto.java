package com.palangwi.soup.admin.dto.email;

import com.palangwi.soup.user.domain.User;

import java.time.LocalDateTime;

public record EmailTestResponseDto(String email, LocalDateTime sentAt) {
    public static EmailTestResponseDto of(User user, LocalDateTime sentAt) {
        return new EmailTestResponseDto(user.getEmail(), sentAt);
    }
}
