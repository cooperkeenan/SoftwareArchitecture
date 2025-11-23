package napier.destore.inventory.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.event.EventTopics;
import napier.destore.common.event.LowStockEvent;
import napier.destore.inventory.domain.StockItem;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishLowStockAlert(StockItem stockItem) {
        try {
            String eventType = stockItem.isOutOfStock() 
                    ? EventTopics.INVENTORY_OUT_OF_STOCK 
                    : EventTopics.INVENTORY_LOW_STOCK;

            LowStockEvent event = LowStockEvent.create(
                    stockItem.getStoreId(),
                    stockItem.getProductId(),
                    stockItem.getProductSku(),
                    stockItem.getProductName(),
                    stockItem.getAvailableQuantity(),
                    stockItem.getLowStockThreshold()
            );
            event.setReorderQuantity(stockItem.getReorderQuantity());
            event.setWarehouseId(stockItem.getWarehouse() != null ? stockItem.getWarehouse().getId() : null);

            rabbitTemplate.convertAndSend(
                    EventTopics.DESTORE_EXCHANGE,
                    eventType,
                    event
            );

            log.info("Published {} event for product {} at store {}: {} units remaining",
                    stockItem.isOutOfStock() ? "OUT_OF_STOCK" : "LOW_STOCK",
                    stockItem.getProductSku(),
                    stockItem.getStoreId(),
                    stockItem.getAvailableQuantity());

        } catch (Exception e) {
            log.error("Failed to publish low stock event for product {}: {}",
                    stockItem.getProductId(), e.getMessage());
        }
    }
}