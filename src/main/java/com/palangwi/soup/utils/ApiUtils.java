package com.palangwi.soup.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ApiUtils {

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, data, null);
    }

    public static <T> ApiResult<Void> success() {
        return new ApiResult<>(true, null, null);
    }

    public static ApiResult<?> error(Throwable throwable, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(throwable, status));
    }

    public static ApiResult<?> error(String message, HttpStatus status) {
        return new ApiResult<>(false, null, new ApiError(message, status));
    }

    @Getter
    public static class ApiError {

        private final String message;
        private final int status;

        ApiError(Throwable throwable, HttpStatus status) {
            this(throwable.getMessage(), status);
        }

        ApiError(String message, HttpStatus status) {
            this.message = message;
            this.status = status.value();
        }

    }

    @Getter
    public static class ApiResult<T> {

        private final boolean success;
        private final T data;
        private final ApiError error;

        private ApiResult(boolean success, T data, ApiError error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }

    }
}
