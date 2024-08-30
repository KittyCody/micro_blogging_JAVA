package io.micro_blogger.server.repository;

import io.micro_blogger.server.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientId(UUID recipientId);
}
