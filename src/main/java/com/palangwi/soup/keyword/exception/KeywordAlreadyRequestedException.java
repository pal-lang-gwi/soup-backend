package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class KeywordAlreadyRequestedException extends BaseCustomException {
  @Override
  public String getMessage() {
    return KeywordExceptionMessage.KEYWORD_ALREADY_REQUESTED.getMessage();
  }

  public HttpStatus getStatus() {
    return KeywordExceptionMessage.KEYWORD_ALREADY_REQUESTED.getStatus();
  }
}
