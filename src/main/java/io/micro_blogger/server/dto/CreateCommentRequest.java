package io.micro_blogger.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCommentRequest {

    @NotBlank(message = "content:must_not_be_blank")
    private String content;
}
