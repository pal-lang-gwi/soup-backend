package com.palangwi.soup.news.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NewsNotFoundException extends BaseCustomException {
    @Override
    public String getMessage() {
        return NewsExceptionMessage.NEWS_NOT_FOUND.getMessage();
    }

    public HttpStatus getStatus() {
        return NewsExceptionMessage.NEWS_NOT_FOUND.getStatus();
    }
}
