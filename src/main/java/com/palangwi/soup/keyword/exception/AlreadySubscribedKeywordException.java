package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AlreadySubscribedKeywordException extends BaseCustomException {
    private final String alreadySubscribedKeywords;

    public AlreadySubscribedKeywordException(String alreadySubscribedKeywords) {
        this.alreadySubscribedKeywords = alreadySubscribedKeywords;
    }

    @Override
    public String getMessage() {
        return KeywordExceptionMessage.ALREADY_SUBSCRIBED_KEYWORD.getMessage() + ": "
                + String.join(", ", alreadySubscribedKeywords);
    }

    @Override
    public HttpStatus getStatus() {
        return KeywordExceptionMessage.ALREADY_SUBSCRIBED_KEYWORD.getStatus();
    }
}