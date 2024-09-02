package io.micro_blogger.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotBlank(message = "description:must_not_be_blank")
    @Size(max = 255, message = "description:too_long")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "imageUrl:must_not_be_null")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "liked_user")
    private Set<String> likedUsers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public List<String> getLikedUsernames() {
        return new ArrayList<>(likedUsers);
    }
}
