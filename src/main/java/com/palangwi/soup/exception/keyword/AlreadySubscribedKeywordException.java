package com.palangwi.soup.exception.keyword;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.palangwi.soup.exception.BaseCustomException;

public class AlreadySubscribedKeywordException extends BaseCustomException {
    private final List<String> alreadySubscribedKeywords;

    public AlreadySubscribedKeywordException(List<String> alreadySubscribedKeywords) {
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