package io.micro_blogger.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreatePostRequest(
        @NotBlank(message = "description:must_not_be_blank")
        @Size(max = 255, message = "description:too_long")
        String description,

        Set<String> tags
) {
}

