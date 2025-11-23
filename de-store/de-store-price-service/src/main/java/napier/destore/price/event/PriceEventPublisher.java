package napier.destore.price.event;

import java.math.BigDecimal;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.event.EventTopics;
import napier.destore.common.event.PriceChangeEvent;
import napier.destore.price.domain.Product;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPriceChange(
        Long storeId, 
        Product product, 
        BigDecimal oldPrice, 
        BigDecimal newPrice,
        String reason) {

        try {
            PriceChangeEvent event = PriceChangeEvent.create(
                    storeId,
                    product.getId(),
                    product.getSku(),
                    product.getName(),
                    oldPrice,
                    newPrice,
                    reason
            );

            rabbitTemplate.convertAndSend(
                    EventTopics.DESTORE_EXCHANGE,
                    EventTopics.PRICE_CHANGED,
                    event
            );

            log.info("Published price change event: {} {} -> {} ({})",
                    product.getSku(), oldPrice, newPrice, reason);

        } catch (Exception e) {
            log.error("Failed to publish price change event for product {}: {}",
                    product.getId(), e.getMessage());
        }
    }
}