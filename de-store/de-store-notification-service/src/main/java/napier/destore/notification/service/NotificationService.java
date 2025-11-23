package napier.destore.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.notification.domain.Notification;
import napier.destore.notification.repository.NotificationRepository;
import napier.destore.notification.sender.NotificationSender;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> notificationSenders;

    @Transactional
    public void sendNotification(Notification notification) {
        // Save to database first
        Notification savedNotification = notificationRepository.save(notification);

        // Find appropriate sender for the channel
        NotificationSender sender = notificationSenders.stream()
                .filter(s -> s.supports(savedNotification.getChannel()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No sender found for channel: " + savedNotification.getChannel()));

        // Send notification
        boolean sent = sender.send(savedNotification);

        // Update status
        if (sent) {
            savedNotification.setStatus(Notification.NotificationStatus.SENT);
            savedNotification.setSentAt(LocalDateTime.now());
        } else {
            savedNotification.setStatus(Notification.NotificationStatus.FAILED);
        }
        notificationRepository.save(savedNotification);

        log.info("Notification {} sent via {} to store {}", 
                savedNotification.getId(), 
                savedNotification.getChannel(), 
                savedNotification.getStoreId());
    }

    public List<Notification> getNotificationsForStore(Long storeId) {
        return notificationRepository.findByStoreId(storeId);
    }

    public List<Notification> getRecentNotifications(Long storeId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return notificationRepository.findByStoreIdAndCreatedAtAfter(storeId, since);
    }
}
