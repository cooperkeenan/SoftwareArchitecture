package napier.destore.notification.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.event.LowStockEvent;
import napier.destore.notification.domain.Notification;
import napier.destore.notification.service.NotificationService;

/**
 * Listens to low stock events from Inventory Service
 * and sends notifications to store managers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LowStockListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "#{lowStockQueue.name}")
    public void handleLowStockEvent(LowStockEvent event) {
        log.info("Received low stock event: {} at store {}", 
                event.getProductSku(), 
                event.getStoreId());

        Notification notification = Notification.builder()
                .storeId(event.getStoreId())
                .recipient("Store Manager")
                .recipientEmail("manager@store" + event.getStoreId() + ".destore.com")
                .type(event.isOutOfStock() 
                        ? Notification.NotificationType.OUT_OF_STOCK_ALERT 
                        : Notification.NotificationType.LOW_STOCK_ALERT)
                .channel(Notification.NotificationChannel.CONSOLE)
                .subject(event.isOutOfStock() ? "URGENT: Product Out of Stock" : "Low Stock Alert")
                .message(event.getAlertMessage())
                .priority(event.isOutOfStock() 
                        ? Notification.NotificationPriority.URGENT 
                        : Notification.NotificationPriority.HIGH)
                .sourceEventId(event.getEventId())
                .build();

        notificationService.sendNotification(notification);
    }
}