package com.palangwi.soup.mail.dto;

import com.palangwi.soup.mail.domain.MailType;
import com.palangwi.soup.user.domain.User;

public record MailMessage(Long userId, String to, String subject, String text, MailType type, Long mailEventId) {
    public static MailMessage of(User user, String subject, String text, MailType type, Long mailEventId) {
        return new MailMessage(user.getId(), user.getEmail(), subject, text, type, mailEventId);
    }
}
