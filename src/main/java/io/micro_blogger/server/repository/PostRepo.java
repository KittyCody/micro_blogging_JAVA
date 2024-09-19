package io.micro_blogger.server.repository;

import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepo extends JpaRepository<Post, UUID> {
    Page<Post> findByAccount(Account account, Pageable pageable);

    List<Post> findByTagsContaining(String tag);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :postId")
    Optional<Post> findByIdWithComments(@Param("postId") UUID postId);

    List<Post> findTop10ByOrderByCreatedAtDesc();

    List<Post> findTop10ByAccountOrderByCreatedAtDesc(Account account);

    List<Post> searchByDescription(String keyword);

}
