package app.repository;

import app.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByForAdmin(boolean forAdmin);
    List<Notification> findAllByReadNotificationAndForAdmin(boolean read, boolean forAdmin);
}
