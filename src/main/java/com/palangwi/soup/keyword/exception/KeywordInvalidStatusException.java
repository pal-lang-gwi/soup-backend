package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class KeywordInvalidStatusException extends BaseCustomException {
    @Override
    public String getMessage() {
        return KeywordExceptionMessage.KEYWORD_INVALID_STATUS_EXCEPTION.getMessage();
    }

    public HttpStatus getStatus() {
        return KeywordExceptionMessage.KEYWORD_INVALID_STATUS_EXCEPTION.getStatus();
    }
}
