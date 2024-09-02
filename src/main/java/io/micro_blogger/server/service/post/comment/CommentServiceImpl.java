package io.micro_blogger.server.service.post.comment;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.CreateCommentRequest;
import io.micro_blogger.server.dto.UpdateCommentRequest;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.Comment;
import io.micro_blogger.server.model.NotificationType;
import io.micro_blogger.server.model.Post;
import io.micro_blogger.server.repository.CommentRepo;
import io.micro_blogger.server.repository.LikeRepo;
import io.micro_blogger.server.repository.PostRepo;
import io.micro_blogger.server.service.account.AccountService;
import io.micro_blogger.server.service.follow.FollowService;
import io.micro_blogger.server.service.notification.NotificationService;
import io.micro_blogger.server.viewmodel.CommentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepository;
    private final PostRepo postRepository;
    private final AccountService accountService;
    private final FollowService followService;
    private final LikeRepo likeRepo;
    private final NotificationService notificationService;

    @Autowired
    public CommentServiceImpl(CommentRepo commentRepository, PostRepo postRepository,
                              AccountService accountService, FollowService followService,
                              LikeRepo likeRepo, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.accountService = accountService;
        this.followService = followService;
        this.likeRepo = likeRepo;
        this.notificationService = notificationService;
    }

    @Override
    public Result<CommentViewModel> addCommentToPost(UUID postId, UUID accountId,
                                                     CreateCommentRequest createCommentRequest) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Post post = postOptional.get();
        Account account = accountOptional.get();
        UUID postCreatorId = post.getAccount().getId();

        if (followService.isFollower(accountId, postCreatorId)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAccount(account);
        comment.setContent(createCommentRequest.getContent());
        comment.setCreatedAt(LocalDateTime.now());

        comment = commentRepository.save(comment);

        notificationService.sendNotification(postCreatorId, accountId, NotificationType.COMMENT, postId, comment.getId(),
                account.getUsername() + " commented on your post.");

        return Result.success(convertToCommentViewModel(comment));
    }

    @Override
    public Result<CommentViewModel> addReplyToComment(UUID parentCommentId, UUID accountId,
                                                      CreateCommentRequest createCommentRequest) {
        Optional<Object> parentCommentOptional = commentRepository.findById(parentCommentId);
        if (parentCommentOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Comment parentComment = (Comment) parentCommentOptional.get();
        Account account = accountOptional.get();

        Comment reply = new Comment();
        reply.setPost(parentComment.getPost());
        reply.setAccount(account);
        reply.setParentComment(parentComment);
        reply.setContent(createCommentRequest.getContent());
        reply.setCreatedAt(LocalDateTime.now());

        reply = commentRepository.save(reply);

        notificationService.sendNotification(parentComment.getAccount().getId(), accountId, NotificationType.COMMENT, parentComment.getPost().getId(), reply.getId(),
                account.getUsername() + " replied to your comment.");

        if (parentComment.getParentComment() != null) {
            notificationService.sendNotification(parentComment.getParentComment().getAccount().getId(), accountId, NotificationType.COMMENT, parentComment.getPost().getId(), reply.getId(),
                    account.getUsername() + " replied to a reply you made.");
        }

        return Result.success(convertToCommentViewModel(reply));
    }

    @Override
    public Result<CommentViewModel> updateComment(UUID commentId, UUID accountId,
                                                  UpdateCommentRequest updateCommentRequest) {
        Optional<Object> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Comment comment = (Comment) commentOptional.get();
        if (!comment.getAccount().getId().equals(accountId)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        if (updateCommentRequest.getContent() != null) {
            comment.setContent(updateCommentRequest.getContent());
        }

        comment = commentRepository.save(comment);
        return Result.success(convertToCommentViewModel(comment));
    }

    @Override
    public ApiError deleteComment(UUID commentId, UUID accountId) {
        Optional<Object> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            return CommonErrors.ENTITY_NOT_PRESENT;
        }

        Comment comment = (Comment) commentOptional.get();
        boolean isCommentCreator = comment.getAccount().getId().equals(accountId);
        boolean isPostCreator = comment.getPost().getAccount().getId().equals(accountId);

        if (!isCommentCreator && !isPostCreator) {
            return CommonErrors.DELETE_FORBIDDEN;
        }

        commentRepository.delete(comment);
        return null;
    }

    @Override
    public List<CommentViewModel> getCommentsByPost(UUID postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return List.of();
        }

        Post post = postOptional.get();
        List<Comment> comments = commentRepository.findByPost(post);
        return comments.stream()
                .map(this::convertToCommentViewModel)
                .collect(Collectors.toList());
    }

    private CommentViewModel convertToCommentViewModel(Comment comment) {
        List<CommentViewModel> replyViewModels = comment.getReplies().stream()
                .map(this::convertToCommentViewModel)
                .collect(Collectors.toList());

        return new CommentViewModel(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUsername(),
                replyViewModels,
                (int) likeRepo.countByComment(comment),
                new ArrayList<>(comment.getLikedUsers())
        );
    }
}
