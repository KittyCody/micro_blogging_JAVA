package io.micro_blogger.server.service.notification;

import io.micro_blogger.server.common.Result;
import io.micro_blogger.server.model.NotificationType;
import io.micro_blogger.server.viewmodel.NotificationViewModel;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void sendNotification(UUID recipientId, UUID senderId, NotificationType type,
                          UUID postId, UUID commentId, String message);

    Result<List<NotificationViewModel>> getNotificationsForUser(UUID userId);
}
