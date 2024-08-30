package io.micro_blogger.server.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterAccountRequest(
        @Size(min = 3, max = 25, message = "username:invalid_size")
        @Pattern(regexp = "^(?!.*\\s)(?!.*\\.$)(?!^\\.)(?!^[0-9]+$)([a-zA-Z0-9_.-]+)$", message = "username:invalid_format")
        String username,

        @Size(min = 6, message = "password:password_too_short")
        String password
) {
}