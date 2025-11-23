package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    private Long id;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String recipientEmail;

    private String recipientPhone;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message is required")
    private String message;

    private NotificationPriority priority;

    private NotificationStatus status;

    private Map<String, Object> metadata;

    private String sourceEventId;

    private LocalDateTime sentAt;

    private LocalDateTime readAt;

    private LocalDateTime createdAt;

    public enum NotificationType {
        LOW_STOCK_ALERT,
        OUT_OF_STOCK_ALERT,
        STOCK_REPLENISHED,
        FINANCE_APPLICATION_APPROVED,
        FINANCE_APPLICATION_DECLINED,
        PRICE_CHANGE,
        PROMOTION_STARTED,
        PROMOTION_ENDING,
        SYSTEM_ALERT,
        REPORT_READY
    }

    public enum NotificationChannel {
        EMAIL,
        SMS,
        IN_APP,
        CONSOLE
    }

    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        READ,
        FAILED
    }
}