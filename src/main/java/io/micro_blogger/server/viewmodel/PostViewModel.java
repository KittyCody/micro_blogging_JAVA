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
public class PostViewModel {
    private UUID id;
    private String accountUsername;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String description;
    private List<String> tags;
    private int postLikes;
    private List<CommentViewModel> comments;
    private List<String> likedUsernames;
}


