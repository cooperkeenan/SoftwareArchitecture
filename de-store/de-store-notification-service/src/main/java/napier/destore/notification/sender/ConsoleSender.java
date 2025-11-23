package napier.destore.notification.sender;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import napier.destore.notification.domain.Notification;

/**
 * Console-based notification sender for prototype/development.
 * In production, this would be replaced with real email/SMS services.
 */
@Component
@Slf4j
public class ConsoleSender implements NotificationSender {

    @Override
    public boolean send(Notification notification) {
        log.info("\n" +
                "╔══════════════════════════════════════════════════════════════╗\n" +
                "║  NOTIFICATION TO STORE MANAGER                               ║\n" +
                "╠══════════════════════════════════════════════════════════════╣\n" +
                "║  Store ID:  {}\n" +
                "║  Recipient: {}\n" +
                "║  Type:      {}\n" +
                "║  Priority:  {}\n" +
                "║  Subject:   {}\n" +
                "║  Message:   {}\n" +
                "╚══════════════════════════════════════════════════════════════╝\n",
                notification.getStoreId(),
                notification.getRecipient(),
                notification.getType(),
                notification.getPriority(),
                notification.getSubject(),
                notification.getMessage()
        );
        return true;
    }

    @Override
    public boolean supports(Notification.NotificationChannel channel) {
        return channel == Notification.NotificationChannel.CONSOLE;
    }
}