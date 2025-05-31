package com.palangwi.soup.exception.news;

import com.palangwi.soup.exception.BaseCustomException;
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
