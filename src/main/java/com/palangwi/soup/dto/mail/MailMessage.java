package com.palangwi.soup.dto.mail;

import com.palangwi.soup.domain.mail.MailType;
import com.palangwi.soup.domain.user.User;

public record MailMessage(Long userId, String to, String subject, String text, MailType type, Long mailEventId) {
    public static MailMessage of(User user, String subject, String text, MailType type, Long mailEventId) {
        return new MailMessage(user.getId(), user.getEmail(), subject, text, type, mailEventId);
    }
}
