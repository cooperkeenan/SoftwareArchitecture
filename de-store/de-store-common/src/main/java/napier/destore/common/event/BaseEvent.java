package napier.destore.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent implements DomainEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime occurredAt;
    private String source;
    private Long storeId;

    protected void initializeDefaults(String source, String eventType) {
        if (this.eventId == null) {
            this.eventId = UUID.randomUUID().toString();
        }
        if (this.occurredAt == null) {
            this.occurredAt = LocalDateTime.now();
        }
        if (this.source == null) {
            this.source = source;
        }
        if (this.eventType == null) {
            this.eventType = eventType;
        }
    }
}