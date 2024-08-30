package io.micro_blogger.server.repository;

import io.micro_blogger.server.model.Account;
import io.micro_blogger.server.model.Comment;
import io.micro_blogger.server.model.Like;
import io.micro_blogger.server.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepo extends JpaRepository<Like, UUID> {
    Optional<Like> findByAccountAndPost(Account account, Post post);

    Optional<Like> findByAccountAndComment(Account account, Comment comment);

    long countByPost(Post post);

    long countByComment(Comment comment);

    void deleteByComment(Comment comment);

    void deleteByPost(Post post);

    List<Like> findByComment(Comment comment);
}