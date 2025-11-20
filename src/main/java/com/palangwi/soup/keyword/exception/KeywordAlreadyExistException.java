package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class KeywordAlreadyExistException extends BaseCustomException {
  @Override
  public String getMessage() {
    return KeywordExceptionMessage.KEYWORD_ALREADY_EXIST.getMessage();
  }

  public HttpStatus getStatus() {
    return KeywordExceptionMessage.KEYWORD_ALREADY_EXIST.getStatus();
  }
}
