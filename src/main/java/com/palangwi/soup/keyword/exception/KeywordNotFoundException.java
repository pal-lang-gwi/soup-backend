package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class KeywordNotFoundException extends BaseCustomException {
  @Override
  public String getMessage() {
    return KeywordExceptionMessage.KEYWORD_NOT_FOUND.getMessage();
  }

  public HttpStatus getStatus() {
    return KeywordExceptionMessage.KEYWORD_NOT_FOUND.getStatus();
  }
}
