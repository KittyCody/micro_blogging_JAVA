package io.micro_blogger.server.service.post.like;

import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.model.*;
import io.micro_blogger.server.repository.AccountRepo;
import io.micro_blogger.server.repository.CommentRepo;
import io.micro_blogger.server.repository.LikeRepo;
import io.micro_blogger.server.repository.PostRepo;
import io.micro_blogger.server.service.account.AccountService;
import io.micro_blogger.server.service.follow.FollowService;
import io.micro_blogger.server.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepo likeRepository;
    private final PostRepo postRepository;
    private final CommentRepo commentRepository;
    private final AccountRepo accountRepository;
    private final FollowService followService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    @Autowired
    public LikeServiceImpl(LikeRepo likeRepository, PostRepo postRepository,
                           CommentRepo commentRepository, AccountRepo accountRepository,
                           FollowService followService, AccountService accountService,
                           NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.accountRepository = accountRepository;
        this.followService = followService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @Override
    public Result<Void> like_unlike_post(UUID postId, UUID accountId) {
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

        if (!accountId.equals(postCreatorId) && !followService.isFollower(accountId, postCreatorId)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        boolean alreadyLiked = post.getLikedUsernames().contains(account.getUsername());

        if (alreadyLiked) {
            post.getLikedUsers().remove(account.getUsername());
            notificationService.sendNotification(postCreatorId, accountId, NotificationType.UNLIKE, postId, null,
                    account.getUsername() + " unliked your post.");
        } else {
            post.getLikedUsers().add(account.getUsername());
            notificationService.sendNotification(postCreatorId, accountId, NotificationType.LIKE, postId, null,
                    account.getUsername() + " liked your post.");
        }

        postRepository.save(post);
        return Result.success(null);
    }

    @Override
    public Result<Void> like_unlike_comment(UUID commentId, UUID accountId) {
        Account account = getAccount(accountId);
        Comment comment = getComment(commentId);
        Post post = comment.getPost();
        UUID postCreatorId = post.getAccount().getId();

        if (!accountId.equals(postCreatorId) && !followService.isFollower(accountId, postCreatorId)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        Optional<Like> existingLike = likeRepository.findByAccountAndComment(account, comment);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            notificationService.sendNotification(comment.getAccount().getId(), accountId, NotificationType.UNLIKE, post.getId(), commentId,
                    account.getUsername() + " unliked your comment.");

            if (comment.getParentComment() != null) {
                notificationService.sendNotification(comment.getParentComment().getAccount().getId(), accountId, NotificationType.UNLIKE, post.getId(), commentId,
                        account.getUsername() + " unliked your reply.");
            }
        } else {
            likeRepository.save(createLike(account, post, comment));
            notificationService.sendNotification(comment.getAccount().getId(), accountId, NotificationType.LIKE, post.getId(), commentId,
                    account.getUsername() + " liked your comment.");

            if (comment.getParentComment() != null) {
                notificationService.sendNotification(comment.getParentComment().getAccount().getId(), accountId, NotificationType.LIKE, post.getId(), commentId,
                        account.getUsername() + " liked your reply.");
            }
        }

        return Result.success(null);
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException(CommonErrors.ENTITY_NOT_PRESENT.getMessage()));
    }

    private Comment getComment(UUID commentId) {
        return (Comment) commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException(CommonErrors.ENTITY_NOT_PRESENT.getMessage()));
    }

    private Like createLike(Account account, Post post, Comment comment) {
        Like like = new Like();
        like.setAccount(account);
        like.setPost(post);
        like.setComment(comment);
        like.setCreatedAt(LocalDateTime.now());
        return like;
    }
}
