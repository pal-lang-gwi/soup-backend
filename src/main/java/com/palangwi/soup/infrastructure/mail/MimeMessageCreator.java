package com.palangwi.soup.infrastructure.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public abstract class MimeMessageCreator<T> {

    protected static final String FROM_EMAIL = "soup <noreply@soup.kr>";
    protected static final String TITLE_PREFIX = "[soup] %s";

    public abstract MimeMessage createMimeMessage(MimeMessage mimeMessage, T message) throws MessagingException;
}