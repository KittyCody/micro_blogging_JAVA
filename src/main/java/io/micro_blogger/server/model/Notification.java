package io.micro_blogger.server.model;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID recipientId;
    private UUID senderId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Long postId;
    private Long commentId;

    private String message;

    private LocalDateTime createdAt;

    public void setCommentId(UUID commentId) {

    }

    public void setPostId(UUID postId) {

    }
}
