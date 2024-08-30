package io.micro_blogger.server.service.post;

import io.micro_blogger.server.common.ApiError;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.dto.CreatePostRequest;
import io.micro_blogger.server.dto.UpdatePostRequest;
import io.micro_blogger.server.model.Post;
import io.micro_blogger.server.viewmodel.PostViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface PostService {

    Result<PostViewModel> createPost(UUID accountId, CreatePostRequest createPostRequest, MultipartFile imageFile) throws IOException;

    Result<List<PostViewModel>> getPostsByAccount(UUID accountId);

    List<PostViewModel> getTopRecentPosts();

    List<PostViewModel> searchPostsByTagsContaining(String tag);

    List<PostViewModel> searchPostsByDescription(String keyword);

    Result<Page<Post>> getPaginatedPostsByAccount(UUID accountId, Pageable pageable);

    ApiError deletePost(UUID accountId, UUID postId);

    Result<PostViewModel> findPostByIdAndAccount(UUID postId, UUID accountId);

    Result<PostViewModel> updatePost(UUID postId, UpdatePostRequest updatePostRequest);

    List<PostViewModel> getTopRecentPostsByAccount(UUID accountId);

}
