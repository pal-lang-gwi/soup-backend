package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class SubscribedKeywordLimitExceededException extends BaseCustomException {
  @Override
  public String getMessage() {
    return KeywordExceptionMessage.SUBSCRIBED_KEYWORD_LIMIT_EXCEEDED.getMessage();
  }

  public HttpStatus getStatus() {
    return KeywordExceptionMessage.SUBSCRIBED_KEYWORD_LIMIT_EXCEEDED.getStatus();
  }
}
