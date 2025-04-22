package com.palangwi.soup.exception.user;

import com.palangwi.soup.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class DuplicateNicknameException extends BaseCustomException {
  @Override
  public String getMessage() {
    return UserExceptionMessage.USER_NICKNAME_DUPLICATED.getMessage();
  }
  public HttpStatus getStatus() {
    return UserExceptionMessage.USER_NICKNAME_DUPLICATED.getStatus();
  }
}
