package napier.destore.inventory.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.inventory.domain.StockItem;
import napier.destore.inventory.event.InventoryEventPublisher;
import napier.destore.inventory.repository.StockRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMonitorService {

    private final StockRepository stockRepository;
    private final InventoryEventPublisher eventPublisher;

    /**
     * Periodically check for low stock items and publish alerts.
     * Runs every 5 minutes.
     */
    @Scheduled(fixedRate = 300000)
    public void checkLowStockItems() {
        log.debug("Running scheduled low stock check...");

        List<StockItem> lowStockItems = stockRepository.findByStatus(StockItem.StockStatus.LOW_STOCK);
        List<StockItem> outOfStockItems = stockRepository.findByStatus(StockItem.StockStatus.OUT_OF_STOCK);

        for (StockItem item : lowStockItems) {
            eventPublisher.publishLowStockAlert(item);
        }

        for (StockItem item : outOfStockItems) {
            eventPublisher.publishLowStockAlert(item);
        }

        if (!lowStockItems.isEmpty() || !outOfStockItems.isEmpty()) {
            log.info("Low stock check complete. Low: {}, Out of stock: {}",
                    lowStockItems.size(), outOfStockItems.size());
        }
    }
}