package io.micro_blogger.server.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentViewModel {

    private UUID id;
    private String content;
    private LocalDateTime createdAt;
    private String username;
    private List<CommentViewModel> replies;
    private int commentLikes;
    private List<String> likedUsernames;

}
