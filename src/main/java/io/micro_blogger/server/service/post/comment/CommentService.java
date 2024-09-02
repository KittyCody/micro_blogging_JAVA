package io.micro_blogger.server.service.post.comment;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.CreateCommentRequest;
import io.micro_blogger.server.dto.UpdateCommentRequest;
import io.micro_blogger.server.viewmodel.CommentViewModel;
import java.util.List;
import java.util.UUID;

public interface CommentService {

    Result<CommentViewModel> addCommentToPost(UUID postId, UUID accountId, CreateCommentRequest createCommentRequest);

    Result<CommentViewModel> updateComment(UUID commentId, UUID accountId, UpdateCommentRequest updateCommentRequest);

    ApiError deleteComment(UUID commentId, UUID accountId);

    List<CommentViewModel> getCommentsByPost(UUID postId);

    Result<CommentViewModel> addReplyToComment(UUID parentCommentId, UUID accountId, CreateCommentRequest request);
}
