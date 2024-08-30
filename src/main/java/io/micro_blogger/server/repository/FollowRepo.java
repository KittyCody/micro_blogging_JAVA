package io.micro_blogger.server.repository;

import io.micro_blogger.server.model.Follow;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepo extends CrudRepository<Follow, Long> {
    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    int countByFolloweeId(UUID followeeId);

    int countByFollowerId(UUID followerId);

    List<Follow> findByFollowerId(UUID id);

    List<Follow> findByFolloweeId(UUID id);

    Follow findByFollowerIdAndFolloweeId(UUID id, UUID id1);

    void flush();
}
