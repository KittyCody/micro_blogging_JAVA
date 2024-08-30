package io.micro_blogger.server.common;

public record ApiError(
        String code,
        String message
) {

}
