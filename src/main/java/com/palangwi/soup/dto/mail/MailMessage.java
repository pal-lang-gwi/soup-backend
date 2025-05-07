package com.palangwi.soup.dto.mail;

import com.palangwi.soup.domain.mail.MailType;

public record MailMessage(Long userId, String to, String subject, String text, MailType type, Long mailEventId) {
}
