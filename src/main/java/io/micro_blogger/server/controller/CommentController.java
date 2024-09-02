package io.micro_blogger.server.controller;
import io.micro_blogger.server.dto.CreateCommentRequest;
import io.micro_blogger.server.dto.UpdateCommentRequest;
import io.micro_blogger.server.service.post.comment.CommentService;
import io.micro_blogger.server.viewmodel.CommentViewModel;
import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Result<CommentViewModel> addComment(
            @PathVariable UUID postId,
            @RequestParam UUID accountId,
            @RequestBody CreateCommentRequest createCommentRequest) {
        return commentService.addCommentToPost(postId, accountId, createCommentRequest);
    }

    @PostMapping("/{parentCommentId}/reply")
    public ResponseEntity<Result<CommentViewModel>> addReplyToComment(
            @PathVariable UUID parentCommentId,
            @RequestParam UUID accountId,
            @RequestBody CreateCommentRequest request, @PathVariable String postId) {

        Result<CommentViewModel> result = commentService.addReplyToComment(parentCommentId, accountId, request);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{commentId}")
    public Result<CommentViewModel> updateComment(
            @PathVariable UUID commentId,
            @RequestParam UUID accountId,
            @RequestBody UpdateCommentRequest updateCommentRequest, @PathVariable String postId) {
        return commentService.updateComment(commentId, accountId, updateCommentRequest);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID accountId, @PathVariable String postId) {
        ApiError error = commentService.deleteComment(commentId, accountId);
        if (error != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<CommentViewModel> getCommentsByPost(@PathVariable UUID postId) {
        return commentService.getCommentsByPost(postId);
    }
}
