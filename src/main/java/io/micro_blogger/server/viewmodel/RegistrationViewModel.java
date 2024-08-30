package io.micro_blogger.server.viewmodel;

import java.util.Date;
import java.util.UUID;

public record RegistrationViewModel(
        UUID id,
        String username,
        Date createdAt
) {
}