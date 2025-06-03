package com.palangwi.soup.exception.mail;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MailExceptionMessage {
    MAIL_EVENT_NOT_FOUND("메일 기록이 존재하지 않습니다.", HttpStatus.NOT_FOUND),;

    private final String message;
    private final HttpStatus status;

    MailExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}