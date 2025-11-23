package napier.destore.notification.sender;

import napier.destore.notification.domain.Notification;

/**
 * Interface for different notification channels.
 * Following Strategy Pattern and Dependency Inversion Principle.
 */
public interface NotificationSender {

    /**
     * Send a notification through this channel.
     * @return true if sent successfully, false otherwise
     */
    boolean send(Notification notification);

    /**
     * Check if this sender supports the given channel.
     */
    boolean supports(Notification.NotificationChannel channel);
}