package com.palangwi.soup.keyword.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class KeywordNotExistException extends BaseCustomException {
  private final List<String> notExistsKeywords;

  public KeywordNotExistException(List<String> notExistsKeywords) {
    this.notExistsKeywords = notExistsKeywords;
  }

  @Override
  public String getMessage() {
    return KeywordExceptionMessage.KEYWORD_NOT_EXIST.getMessage()  + ": "
            + String.join(", ", notExistsKeywords);
  }

  public HttpStatus getStatus() {
    return KeywordExceptionMessage.KEYWORD_NOT_EXIST.getStatus();
  }
}
