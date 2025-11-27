package napier.destore.finance.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.event.EventTopics;
import napier.destore.common.event.FinanceDecisionEvent;
import napier.destore.common.dto.FinanceApplicationDto;
import napier.destore.finance.domain.FinanceApplication;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinanceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishFinanceDecision(FinanceApplication application) {
        try {
            FinanceApplicationDto.ApplicationStatus status = FinanceApplicationDto.ApplicationStatus.valueOf(
                    application.getStatus().name()
            );

            FinanceDecisionEvent event = FinanceDecisionEvent.create(
                    application.getStoreId(),
                    application.getId(),
                    application.getApplicationReference(),
                    application.getCustomerName(),
                    application.getCustomerEmail(),
                    application.getAmount(),
                    status,
                    application.getDecisionReason()
            );

            event.setTermMonths(application.getTermMonths());
            event.setMonthlyPayment(application.getMonthlyPayment());
            event.setExternalReference(application.getExternalReference());

            rabbitTemplate.convertAndSend(
                    EventTopics.DESTORE_EXCHANGE,
                    EventTopics.FINANCE_DECISION,
                    event
            );

            log.info("Published finance decision event: {} - {}",
                    application.getApplicationReference(),
                    application.getStatus());

        } catch (Exception e) {
            log.error("Failed to publish finance decision event for application {}: {}",
                    application.getId(), e.getMessage());
        }
    }
}