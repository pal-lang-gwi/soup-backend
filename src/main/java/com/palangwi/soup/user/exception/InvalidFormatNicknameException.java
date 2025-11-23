package com.palangwi.soup.user.exception;

import com.palangwi.soup.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidFormatNicknameException extends BaseCustomException {
    @Override
    public String getMessage() {
        return UserExceptionMessage.USER_NICKNAME_INVALID_FORMAT.getMessage();
    }

    public HttpStatus getStatus() {
        return UserExceptionMessage.USER_NICKNAME_INVALID_FORMAT.getStatus();
    }
}