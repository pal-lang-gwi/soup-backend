package com.palangwi.soup.exception.keyword;

import com.palangwi.soup.exception.BaseCustomException;
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
