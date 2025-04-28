package com.palangwi.soup.exception.keyword;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum KeywordExceptionMessage {
    KEYWORD_NOT_FOUND("해당 키워드가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_SUBSCRIBED_KEYWORD("구독중인 키워드가 아닙니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

    KeywordExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
