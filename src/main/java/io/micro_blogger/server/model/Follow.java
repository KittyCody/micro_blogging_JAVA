package io.micro_blogger.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID followerId;

    @Column(nullable = false)
    private UUID followeeId;

    @Column(updatable = false, nullable = false)
    private LocalDateTime followedAt;

    public Follow() {
        this.followedAt = LocalDateTime.now();
    }

    public Follow(UUID followerId, UUID followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.followedAt = LocalDateTime.now();
    }
}
