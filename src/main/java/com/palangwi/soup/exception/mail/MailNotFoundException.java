package com.palangwi.soup.exception.mail;

import com.palangwi.soup.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class MailNotFoundException extends BaseCustomException {
    @Override
    public String getMessage() {
        return MailExceptionMessage.MAIL_EVENT_NOT_FOUND.getMessage();
    }

    public HttpStatus getStatus() {
        return MailExceptionMessage.MAIL_EVENT_NOT_FOUND.getStatus();
    }
}
