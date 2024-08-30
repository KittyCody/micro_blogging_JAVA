package io.micro_blogger.server.common;

import lombok.Getter;

public class Result<T> {
    @Getter
    private final T value;
    @Getter
    private final ApiError error;
    private final boolean isSuccess;

    protected Result(T value, ApiError error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T> Result<T> failure(ApiError error) {
        return new Result<>(null, error, false);
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
