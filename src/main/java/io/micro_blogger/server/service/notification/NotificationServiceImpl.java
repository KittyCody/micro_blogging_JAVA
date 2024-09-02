package io.micro_blogger.server.service.notification;
import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.model.Notification;
import io.micro_blogger.server.model.NotificationType;
import io.micro_blogger.server.repository.NotificationRepo;
import io.micro_blogger.server.viewmodel.NotificationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;

    @Autowired
    public NotificationServiceImpl(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Override
    public void sendNotification(UUID recipientId, UUID senderId, NotificationType type,
                                 UUID postId, UUID commentId, String message) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setSenderId(senderId);
        notification.setType(type);
        notification.setPostId(postId);
        notification.setCommentId(commentId);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepo.save(notification);
    }

    @Override
    public Result<List<NotificationViewModel>> getNotificationsForUser(UUID userId) {
        List<Notification> notifications = notificationRepo.findByRecipientId(userId);
        List<NotificationViewModel> notificationViewModels = notifications.stream()
                .map(this::convertToViewModel)
                .collect(Collectors.toList());

        return Result.success(notificationViewModels);
    }

    private NotificationViewModel convertToViewModel(Notification notification) {
        return new NotificationViewModel(
                notification.getId(),
                notification.getRecipientId(),
                notification.getSenderId(),
                notification.getType(),
                notification.getPostId(),
                notification.getCommentId(),
                notification.getMessage(),
                notification.getCreatedAt()
        );
    }
}
