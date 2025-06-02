package com.palangwi.soup.exception.keyword;

import com.palangwi.soup.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AlreadyRejectedKeywordException extends BaseCustomException {
  @Override
  public String getMessage() {
    return KeywordExceptionMessage.ALREADY_REJECTED_KEYWORD.getMessage();
  }

  public HttpStatus getStatus() {
    return KeywordExceptionMessage.ALREADY_REJECTED_KEYWORD.getStatus();
  }
}
