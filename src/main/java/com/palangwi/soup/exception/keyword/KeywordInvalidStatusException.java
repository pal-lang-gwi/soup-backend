package com.palangwi.soup.exception.keyword;

import com.palangwi.soup.exception.BaseCustomException;
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
