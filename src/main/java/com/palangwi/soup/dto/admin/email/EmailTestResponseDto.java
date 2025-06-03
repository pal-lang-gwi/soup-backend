package com.palangwi.soup.dto.admin.email;

import com.palangwi.soup.domain.user.User;
import java.time.LocalDateTime;

public record EmailTestResponseDto(String email, LocalDateTime sentAt) {
    public static EmailTestResponseDto of(User user, LocalDateTime sentAt) {
        return new EmailTestResponseDto(user.getEmail(), sentAt);
    }
}
