package com.palangwi.soup.news.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidNewsIdFormatException extends BaseCustomException {
    @Override
    public String getMessage() {
        return NewsExceptionMessage.INVALID_NEWS_ID_FORMAT.getMessage();
    }

    public HttpStatus getStatus() {
        return NewsExceptionMessage.INVALID_NEWS_ID_FORMAT.getStatus();
    }
}
