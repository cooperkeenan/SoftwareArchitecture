package napier.destore.notification.sender;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import napier.destore.notification.domain.Notification;

/**
 * Email notification sender (stubbed for prototype).
 * In production, would integrate with SendGrid, AWS SES, or similar.
 */
@Component
@Slf4j
public class EmailSender implements NotificationSender {

    @Override
    public boolean send(Notification notification) {
        log.info("EMAIL SENT to {} at {}: {}",
                notification.getRecipient(),
                notification.getRecipientEmail(),
                notification.getSubject());
        // In production: integrate with email service
        return true;
    }

    @Override
    public boolean supports(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.EMAIL;
    }
}