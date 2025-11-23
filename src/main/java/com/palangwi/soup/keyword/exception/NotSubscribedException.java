package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NotSubscribedException extends BaseCustomException {
    @Override
    public String getMessage() {
        return KeywordExceptionMessage.NOT_SUBSCRIBED_KEYWORD.getMessage();
    }

    public HttpStatus getStatus() {
        return KeywordExceptionMessage.NOT_SUBSCRIBED_KEYWORD.getStatus();
    }
}
