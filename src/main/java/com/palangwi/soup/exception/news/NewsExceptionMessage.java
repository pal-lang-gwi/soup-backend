package com.palangwi.soup.exception.news;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NewsExceptionMessage {
    NEWS_NOT_FOUND("해당 뉴스가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

    NewsExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
