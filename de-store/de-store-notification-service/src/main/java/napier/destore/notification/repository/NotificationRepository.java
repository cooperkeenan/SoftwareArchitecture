package napier.destore.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import napier.destore.notification.domain.Notification;
import napier.destore.notification.domain.Notification.NotificationStatus;
import napier.destore.notification.domain.Notification.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStoreId(Long storeId);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByStoreIdAndCreatedAtAfter(Long storeId, LocalDateTime after);
}