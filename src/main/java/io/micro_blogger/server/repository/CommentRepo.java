package io.micro_blogger.server.repository;

import io.micro_blogger.server.model.Comment;
import io.micro_blogger.server.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findByParentComment(Comment parentComment);

    Optional<Object> findById(UUID commentId);
}
