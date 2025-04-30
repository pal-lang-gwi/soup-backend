package com.palangwi.soup.exception.keyword;

import com.palangwi.soup.exception.BaseCustomException;
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
