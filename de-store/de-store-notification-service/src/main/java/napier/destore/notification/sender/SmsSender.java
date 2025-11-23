package napier.destore.notification.sender;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import napier.destore.notification.domain.Notification;

/**
 * SMS notification sender (stubbed for prototype).
 * In production, would integrate with Twilio or similar.
 */
@Component
@Slf4j
public class SmsSender implements NotificationSender {

    @Override
    public boolean send(Notification notification) {
        log.info("SMS SENT to {}: {}",
                notification.getRecipient(),
                notification.getSubject());
        // In production: integrate with SMS service
        return true;
    }

    @Override
    public boolean supports(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.SMS;
    }
}