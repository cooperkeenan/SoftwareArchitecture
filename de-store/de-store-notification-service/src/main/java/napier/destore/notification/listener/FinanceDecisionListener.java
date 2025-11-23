package napier.destore.notification.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.event.FinanceDecisionEvent;
import napier.destore.notification.domain.Notification;
import napier.destore.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens to finance decision events from Finance Gateway Service
 * and sends notifications to store managers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FinanceDecisionListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "#{financeDecisionQueue.name}")
    public void handleFinanceDecisionEvent(FinanceDecisionEvent event) {
        log.info("Received finance decision event: {} for store {}", 
                event.getApplicationReference(), 
                event.getStoreId());

        Notification notification = Notification.builder()
                .storeId(event.getStoreId())
                .recipient("Store Manager")
                .recipientEmail("manager@store" + event.getStoreId() + ".destore.com")
                .type(event.isApproved() 
                        ? Notification.NotificationType.FINANCE_APPLICATION_APPROVED
                        : Notification.NotificationType.FINANCE_APPLICATION_DECLINED)
                .channel(Notification.NotificationChannel.CONSOLE)
                .subject("Finance Application Decision: " + event.getApplicationReference())
                .message(event.getDecisionMessage())
                .priority(Notification.NotificationPriority.MEDIUM)
                .sourceEventId(event.getEventId())
                .build();

        notificationService.sendNotification(notification);
    }
}