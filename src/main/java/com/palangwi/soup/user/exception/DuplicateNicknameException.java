package com.palangwi.soup.user.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
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
