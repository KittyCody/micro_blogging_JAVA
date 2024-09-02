package io.micro_blogger.server.dto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateCommentRequest {
    private String content;
}
