package com.palangwi.soup.user.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseCustomException {
    @Override
    public String getMessage() {
        return UserExceptionMessage.USER_NOT_FOUND.getMessage();
    }

    public HttpStatus getStatus() {
        return UserExceptionMessage.USER_NOT_FOUND.getStatus();
    }
}