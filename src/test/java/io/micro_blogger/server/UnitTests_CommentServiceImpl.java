package io.micro_blogger.server;

import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.CreateCommentRequest;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.Comment;
import io.micro_blogger.server.model.Post;
import io.micro_blogger.server.repository.CommentRepo;
import io.micro_blogger.server.repository.PostRepo;
import io.micro_blogger.server.service.account.AccountService;
import io.micro_blogger.server.service.follow.FollowService;
import io.micro_blogger.server.service.notification.NotificationService;
import io.micro_blogger.server.service.post.comment.CommentServiceImpl;
import io.micro_blogger.server.viewmodel.CommentViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class CommentServiceImplTest {

    @Mock
    private CommentRepo commentRepository;

    @Mock
    private PostRepo postRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private FollowService followService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UUID postId;
    private UUID accountId;
    private CreateCommentRequest createCommentRequest;
    private Post post;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        createCommentRequest = new CreateCommentRequest("Test Comment");

        post = mock(Post.class);
        account = mock(Account.class);
    }

    @Test
    void addCommentToPost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        when(accountService.findById(accountId)).thenReturn(Optional.of(account));

        Account postCreator = mock(Account.class);
        when(post.getAccount()).thenReturn(postCreator);

        UUID postCreatorId = UUID.randomUUID();
        when(postCreator.getId()).thenReturn(postCreatorId);

        String username = "testUser";
        when(account.getUsername()).thenReturn(username);

        when(followService.isFollower(accountId, postCreatorId)).thenReturn(true);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setAccount(account);
        comment.setPost(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Result<CommentViewModel> result = commentService.addCommentToPost(postId, accountId, createCommentRequest);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());

        verify(notificationService, times(1)).sendNotification(any(), any(), any(), any(), any(), any());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

}