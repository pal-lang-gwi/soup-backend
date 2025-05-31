package com.palangwi.soup.exception.keyword;

import com.palangwi.soup.exception.BaseCustomException;
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
