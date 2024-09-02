package io.micro_blogger.server.controller;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.service.post.like.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiError> likePost(@PathVariable UUID postId, @RequestParam UUID accountId) {
        Result<Void> result = likeService.like_unlike_post(postId, accountId);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(CommonErrors.FORBIDDEN_OPERATION);
        }
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ApiError> likeComment(@PathVariable UUID commentId, @RequestParam UUID accountId) {
        Result<Void> result = likeService.like_unlike_comment(commentId, accountId);

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(CommonErrors.FORBIDDEN_OPERATION);
        }
    }
}
