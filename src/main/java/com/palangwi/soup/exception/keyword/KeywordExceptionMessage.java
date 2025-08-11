package com.palangwi.soup.exception.keyword;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum KeywordExceptionMessage {
    KEYWORD_NOT_FOUND("해당 키워드가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_SUBSCRIBED_KEYWORD("구독중인 키워드가 아닙니다.", HttpStatus.BAD_REQUEST),
    ALREADY_SUBSCRIBED_KEYWORD("이미 등록된 키워드입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_REJECTED_KEYWORD("이미 등록이 거절된 키워드입니다.", HttpStatus.BAD_REQUEST),
    KEYWORD_INVALID_STATUS_EXCEPTION("키워드의 상태가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    KEYWORD_ALREADY_REQUESTED("이미 등록 요청한 키워드입니다.", HttpStatus.BAD_REQUEST),
    KEYWORD_ALREADY_EXIST("이미 등록된 키워드입니다.", HttpStatus.BAD_REQUEST),
    KEYWORD_NOT_EXIST("존재하지 않는 키워드입니다.", HttpStatus.BAD_REQUEST),
    SUBSCRIBED_KEYWORD_LIMIT_EXCEEDED("키워드는 최대 10개까지 구독할 수 있습니다.",  HttpStatus.BAD_REQUEST),;

    private final String message;
    private final HttpStatus status;

    KeywordExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
