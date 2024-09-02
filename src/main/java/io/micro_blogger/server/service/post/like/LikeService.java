package io.micro_blogger.server.service.post.like;

import io.micro_blogger.server.common.Result;
import java.util.UUID;

public interface LikeService {
    Result<Void> like_unlike_post(UUID postId, UUID accountId);

    Result<Void> like_unlike_comment(UUID commentId, UUID accountId);
}

