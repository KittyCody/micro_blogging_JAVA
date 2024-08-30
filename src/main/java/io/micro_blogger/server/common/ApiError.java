package io.micro_blogger.server.common;

public record ApiError(
        String code,
        String message
) {

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
