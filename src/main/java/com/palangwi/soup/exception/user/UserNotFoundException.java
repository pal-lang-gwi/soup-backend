package com.palangwi.soup.exception.user;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends RuntimeException {
  @Override
  public String getMessage() {
    return UserExceptionMessage.USER_NOT_FOUND.getMessage();
  }
  public HttpStatus getStatus() {
    return UserExceptionMessage.USER_NOT_FOUND.getStatus();
  }
}
