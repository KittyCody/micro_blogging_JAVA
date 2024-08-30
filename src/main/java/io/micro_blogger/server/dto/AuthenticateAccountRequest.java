package io.micro_blogger.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticateAccountRequest(
        @NotBlank(message = "username:must_not_be_blank")
        @Size(min = 3, max = 30, message = "username:invalid_size")
        String username,

        @Size(min = 6, message = "password:password_too_short")
        @NotBlank(message = "password:must_not_be_blank")
        String password
) {
}