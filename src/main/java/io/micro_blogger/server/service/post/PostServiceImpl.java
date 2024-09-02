package io.micro_blogger.server.service.post;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.CreatePostRequest;
import io.micro_blogger.server.dto.UpdatePostRequest;
import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.Comment;
import io.micro_blogger.server.model.Post;
import io.micro_blogger.server.repository.CommentRepo;
import io.micro_blogger.server.repository.LikeRepo;
import io.micro_blogger.server.repository.PostRepo;
import io.micro_blogger.server.service.account.AccountService;
import io.micro_blogger.server.service.security.S3Service;
import io.micro_blogger.server.viewmodel.PostViewModel;
import io.micro_blogger.server.viewmodel.CommentViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepo postRepository;
    private final S3Service s3Service;
    private final AccountService accountService;
    private final LikeRepo likeRepo;
    private final CommentRepo commentRepo;

    @Autowired
    public PostServiceImpl(PostRepo postRepository, S3Service s3Service, AccountService accountService, LikeRepo likeRepo, CommentRepo commentRepo) {
        this.postRepository = postRepository;
        this.s3Service = s3Service;
        this.accountService = accountService;
        this.likeRepo = likeRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    public Result<PostViewModel> createPost(UUID accountId, CreatePostRequest createPostRequest, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            Result<String> imageUrlResult = this.uploadImage(imageFile);
            if (imageUrlResult.isFailure()) {
                return Result.failure(imageUrlResult.getError());
            }

            Optional<Account> accountOptional = accountService.findById(accountId);
            if (accountOptional.isEmpty()) {
                return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
            }

            Account account = accountOptional.get();
            Post post = new Post();
            post.setImageUrl(imageUrlResult.getValue());
            post.setAccount(account);
            post.setDescription(createPostRequest.description());

            if (createPostRequest.tags() != null) {
                post.setTags(new HashSet<>(createPostRequest.tags()));
            }

            post = postRepository.save(post);

            return Result.success(convertToViewModel(post));
        } else {
            return Result.failure(CommonErrors.NULL_IMAGE_FILE);
        }
    }

    private Result<String> uploadImage(MultipartFile imageFile) throws IOException {
        String imageUrl;
        imageUrl = String.valueOf(s3Service.uploadImage(imageFile));
        return Result.success(imageUrl);
    }

    @Override
    public Result<List<PostViewModel>> getPostsByAccount(UUID accountId) {
        Pageable pageable = PageRequest.of(0, 10);

        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Account account = accountOptional.get();
        Page<Post> postsPage = postRepository.findByAccount(account, pageable);

        List<PostViewModel> results = postsPage.stream()
                .map(this::convertToViewModel)
                .collect(Collectors.toList());

        return Result.success(results);
    }

    @Override
    public List<PostViewModel> searchPostsByTagsContaining(String tag) {
        System.out.println("Searching for posts containing tag: " + tag);
        List<Post> posts = postRepository.findByTagsContaining(tag);
        System.out.println("Found posts: " + posts.size());

        return posts.stream()
                .map(this::convertToViewModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostViewModel> searchPostsByDescription(String keyword) {
        List<Post> posts = postRepository.searchByDescription(keyword);
        return posts.stream()
                .map(this::convertToViewModel)
                .collect(Collectors.toList());
    }

    @Override
    public Result<Page<Post>> getPaginatedPostsByAccount(UUID accountId, Pageable pageable) {
        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Account account = accountOptional.get();
        Page<Post> posts = postRepository.findByAccount(account, pageable);

        return Result.success(posts);
    }

    @Transactional
    @Override
    public ApiError deletePost(UUID accountId, UUID postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return CommonErrors.ENTITY_NOT_PRESENT;
        }

        Post post = postOptional.get();

        if (!post.getAccount().getId().equals(accountId)) {
            return CommonErrors.DELETE_FORBIDDEN;
        }

        likeRepo.deleteByPost(post);
        List<Comment> comments = commentRepo.findByPost(post);
        for (Comment comment : comments) {
            likeRepo.deleteByComment(comment);
            deleteNestedComments(comment);
        }
        commentRepo.deleteAllInBatch(comments);
        postRepository.delete(post);

        return null;
    }

    private void deleteNestedComments(Comment parentComment) {
        List<Comment> childComments = commentRepo.findByParentComment(parentComment);
        for (Comment child : childComments) {
            likeRepo.deleteByComment(child);
            deleteNestedComments(child);
        }
        commentRepo.deleteAll(childComments);
    }

    @Override
    public Result<PostViewModel> findPostByIdAndAccount(UUID postId, UUID accountId) {
        Optional<Post> postOptional = postRepository.findByIdWithComments(postId);
        if (postOptional.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Post post = postOptional.get();
        if (!post.getAccount().getId().equals(accountId)) {
            return Result.failure(CommonErrors.FORBIDDEN_OPERATION);
        }

        return Result.success(convertToViewModel(post));
    }

    @Override
    public Result<PostViewModel> updatePost(UUID postId, UpdatePostRequest updatePostRequest) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return Result.failure(CommonErrors.ENTITY_NOT_PRESENT);
        }

        Post post = optionalPost.get();

        if (updatePostRequest.description() != null) {
            post.setDescription(updatePostRequest.description());
        }

        if (updatePostRequest.tags() != null) {
            post.setTags(new HashSet<>(updatePostRequest.tags()));
        }

        Post updatedPost = postRepository.save(post);
        return Result.success(convertToViewModel(updatedPost));
    }

    @Override
    public List<PostViewModel> getTopRecentPosts() {
        List<Post> recentPosts = postRepository.findTop10ByOrderByCreatedAtDesc();
        return recentPosts.stream()
                .map(this::convertToViewModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostViewModel> getTopRecentPostsByAccount(UUID accountId) {
        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty()) {
            return List.of();
        }

        Account account = accountOptional.get();
        List<Post> recentPosts = postRepository.findTop10ByAccountOrderByCreatedAtDesc(account);
        return recentPosts.stream()
                .map(this::convertToViewModel)
                .collect(Collectors.toList());
    }

    private CommentViewModel convertToCommentViewModel(Comment comment) {
        List<String> likedUsernames = likeRepo.findByComment(comment).stream()
                .map(like -> like.getAccount().getUsername())
                .collect(Collectors.toList());

        List<CommentViewModel> replyViewModels = comment.getReplies().stream()
                .map(this::convertToCommentViewModel)
                .collect(Collectors.toList());

        return new CommentViewModel(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUsername(),
                replyViewModels,
                likedUsernames.size(),
                likedUsernames
        );
    }

    private PostViewModel convertToViewModel(Post post) {
        List<CommentViewModel> commentViewModels = post.getComments().stream()
                .map(this::convertToCommentViewModel)
                .collect(Collectors.toList());

        return new PostViewModel(
                post.getId(),
                post.getAccount().getUsername(),
                post.getCreatedAt(),
                post.getImageUrl(),
                post.getDescription(),
                new ArrayList<>(post.getTags()),
                post.getLikedUsers().size(),
                commentViewModels,
                new ArrayList<>(post.getLikedUsers())
        );
    }
}