package io.micro_blogger.server.viewmodel;

import io.micro_blogger.server.model.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class NotificationViewModel {
    private UUID id;
    private UUID recipientId;
    private UUID senderId;
    private NotificationType type;
    private UUID postId;
    private UUID commentId;
    private String message;
    private LocalDateTime createdAt;

    public NotificationViewModel(UUID id, UUID recipientId, UUID senderId, NotificationType type,
                                 UUID postId, UUID commentId, String message, LocalDateTime createdAt) {
        this.id = id;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.type = type;
        this.postId = postId;
        this.commentId = commentId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public NotificationViewModel(UUID id, UUID recipientId, UUID senderId, NotificationType type, Long postId, Long commentId, String message, LocalDateTime createdAt) {
    }
}
