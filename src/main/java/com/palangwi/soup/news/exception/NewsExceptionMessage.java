package com.palangwi.soup.news.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NewsExceptionMessage {
    NEWS_NOT_FOUND("해당 뉴스가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_NEWS_ID_FORMAT("해당 뉴스의 ID 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),;

    private final String message;
    private final HttpStatus status;

    NewsExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
