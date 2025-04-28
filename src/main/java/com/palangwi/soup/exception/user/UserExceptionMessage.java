package com.palangwi.soup.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserExceptionMessage {
    USER_NOT_FOUND("해당 유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_USER_ROLE("허용되지 않은 유저의 요청입니다.", HttpStatus.FORBIDDEN),
    USER_NICKNAME_DUPLICATED("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    USER_NICKNAME_INVALID_FORMAT("닉네임은 2~10자의 한글, 영문, 숫자만 사용할 수 있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN("토큰이 유효하지 않습니다", HttpStatus.FORBIDDEN),;

    private final String message;
    private final HttpStatus status;

    UserExceptionMessage(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}