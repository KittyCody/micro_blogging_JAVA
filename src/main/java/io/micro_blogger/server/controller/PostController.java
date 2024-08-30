package io.micro_blogger.server.controller;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.CommonErrors;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.CreatePostRequest;
import io.micro_blogger.server.dto.UpdatePostRequest;
import io.micro_blogger.server.model.CustomUserDetails;
import io.micro_blogger.server.model.Post;
import io.micro_blogger.server.service.post.PostService;
import io.micro_blogger.server.viewmodel.PostViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return UUID.fromString(jwt.getSubject());
        }
        throw new IllegalArgumentException("Unable to extract UUID from authentication principal");
    }

    private ResponseEntity<?> handleAuthorization(UUID accountId, Authentication authentication) {
        try {
            UUID currentUserId = getCurrentUserId(authentication);
            if (!accountId.equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonErrors.ACCESS_DENIED);
            }
            return null;
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonErrors.INVALID_UUID);
        }
    }

    private ResponseEntity<?> handleResult(Result<?> result) {
        if (result.isFailure()) {
            ApiError error = result.getError();
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (error == CommonErrors.ENTITY_NOT_PRESENT) {
                status = HttpStatus.NOT_FOUND;
            } else if (error == CommonErrors.FORBIDDEN_OPERATION) {
                status = HttpStatus.FORBIDDEN;
            }
            return ResponseEntity.status(status).body(error);
        }
        return ResponseEntity.ok(result.getValue());
    }

    @PostMapping(value = "/accounts/{accountId}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @PathVariable("accountId") UUID accountId,
            @RequestPart("post") CreatePostRequest createPostRequest,
            @RequestPart(value = "imageFile") MultipartFile imageFile,
            Authentication authentication) throws IOException {

        UUID currentUserId = getCurrentUserId(authentication);
        if (!accountId.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonErrors.ACCESS_DENIED);
        }

        if (imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Image file is required");
        }

        Result<PostViewModel> result = postService.createPost(accountId, createPostRequest, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getValue());
    }

    @GetMapping("/accounts/{accountId}/posts")
    public ResponseEntity<?> getPostsByAccount(@PathVariable("accountId") UUID accountId) {
        Result<List<PostViewModel>> result = postService.getPostsByAccount(accountId);
        return handleResult(result);
    }

    @GetMapping("/accounts/{accountId}/tags")
    public ResponseEntity<List<PostViewModel>> getPostsByTagsContaining(
            @PathVariable("accountId") UUID accountId,
            @RequestParam("tag") String tag) {
        // Ensure the search works regardless of case and leading #
        tag = tag.startsWith("#") ? tag.substring(1) : tag;
        tag = tag.toLowerCase();

        List<PostViewModel> posts = postService.searchPostsByTagsContaining(tag);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/accounts/{accountId}/search")
    public ResponseEntity<List<PostViewModel>> searchPosts(
            @PathVariable UUID accountId,
            @RequestParam String keyword) {
        List<PostViewModel> posts = postService.searchPostsByDescription(keyword);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PostViewModel>> getTopRecentPosts() {
        List<PostViewModel> posts = postService.getTopRecentPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestParam("keyword") String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonErrors.BAD_REQUEST);
        }
        List<PostViewModel> posts = postService.searchPostsByDescription(keyword);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/accounts/{accountId}/paginated")
    public ResponseEntity<?> getPaginatedPosts(
            @PathVariable("accountId") UUID accountId,
            Pageable pageable) {
        Result<Page<Post>> result = postService.getPaginatedPostsByAccount(accountId, pageable);
        if (result.isFailure()) {
            if (result.getError() == CommonErrors.ENTITY_NOT_PRESENT) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(result.getValue());
    }

    @DeleteMapping("/accounts/{accountId}/{postId}")
    public ResponseEntity<?> deletePostById(
            @PathVariable UUID accountId,
            @PathVariable UUID postId,
            Authentication authentication) {

        ResponseEntity<?> authResponse = handleAuthorization(accountId, authentication);
        if (authResponse != null) {
            return authResponse;
        }

        ApiError error = postService.deletePost(accountId, postId);
        if (error != null) {
            HttpStatus status = (error == CommonErrors.ENTITY_NOT_PRESENT) ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(error);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts/{accountId}/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable UUID accountId, @PathVariable UUID postId, Authentication authentication) {
        Result<PostViewModel> result = postService.findPostByIdAndAccount(postId, accountId);
        return handleResult(result);
    }

    @PatchMapping("/accounts/{accountId}/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable UUID accountId,
            @PathVariable UUID postId,
            @RequestBody UpdatePostRequest updatePostRequest,
            Authentication authentication) {

        ResponseEntity<?> authResponse = handleAuthorization(accountId, authentication);
        if (authResponse != null) {
            return authResponse;
        }

        Result<PostViewModel> result = postService.updatePost(postId, updatePostRequest);
        return handleResult(result);
    }
}

