package napier.destore.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.dto.InventoryDto;
import napier.destore.common.exception.ResourceNotFoundException;
import napier.destore.common.exception.ValidationException;
import napier.destore.inventory.domain.StockItem;
import napier.destore.inventory.domain.StockMovement;
import napier.destore.inventory.event.InventoryEventPublisher;
import napier.destore.inventory.repository.StockMovementRepository;
import napier.destore.inventory.repository.StockRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final StockRepository stockRepository;
    private final StockMovementRepository movementRepository;
    private final InventoryEventPublisher eventPublisher;

    @Transactional
    public InventoryDto createStockItem(InventoryDto dto) {
        if (stockRepository.findByProductIdAndStoreId(dto.getProductId(), dto.getStoreId()).isPresent()) {
            throw new ValidationException("Stock item already exists for this product and store");
        }

        StockItem stockItem = StockItem.builder()
                .productId(dto.getProductId())
                .productSku(dto.getProductSku())
                .productName(dto.getProductName())
                .storeId(dto.getStoreId())
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 0)
                .reservedQuantity(0)
                .lowStockThreshold(dto.getLowStockThreshold() != null ? dto.getLowStockThreshold() : 10)
                .reorderQuantity(dto.getReorderQuantity() != null ? dto.getReorderQuantity() : 50)
                .build();

        stockItem = stockRepository.save(stockItem);
        log.info("Created stock item for product {} at store {}", dto.getProductId(), dto.getStoreId());

        checkAndPublishLowStockAlert(stockItem);

        return toDto(stockItem);
    }

    public InventoryDto getStockItem(Long productId, Long storeId) {
        StockItem stockItem = stockRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("StockItem",
                        String.format("productId=%d, storeId=%d", productId, storeId)));
        return toDto(stockItem);
    }

    public List<InventoryDto> getStoreInventory(Long storeId) {
        return stockRepository.findByStoreId(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<InventoryDto> getLowStockItems(Long storeId) {
        return stockRepository.findLowStockItems(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryDto addStock(Long productId, Long storeId, int quantity, String reason) {
        StockItem stockItem = stockRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("StockItem",
                        String.format("productId=%d, storeId=%d", productId, storeId)));

        int previousQuantity = stockItem.getQuantity();
        stockItem.setQuantity(previousQuantity + quantity);
        stockItem.setLastRestocked(LocalDateTime.now());
        stockItem = stockRepository.save(stockItem);

        recordMovement(stockItem, StockMovement.MovementType.STOCK_IN, quantity, previousQuantity, reason);

        log.info("Added {} units to product {} at store {}. New quantity: {}",
                quantity, productId, storeId, stockItem.getQuantity());

        return toDto(stockItem);
    }

    @Transactional
    public InventoryDto removeStock(Long productId, Long storeId, int quantity, String reason) {
        StockItem stockItem = stockRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("StockItem",
                        String.format("productId=%d, storeId=%d", productId, storeId)));

        if (stockItem.getAvailableQuantity() < quantity) {
            throw new ValidationException("Insufficient stock. Available: " + stockItem.getAvailableQuantity());
        }

        int previousQuantity = stockItem.getQuantity();
        stockItem.setQuantity(previousQuantity - quantity);
        stockItem = stockRepository.save(stockItem);

        recordMovement(stockItem, StockMovement.MovementType.STOCK_OUT, quantity, previousQuantity, reason);

        log.info("Removed {} units from product {} at store {}. New quantity: {}",
                quantity, productId, storeId, stockItem.getQuantity());

        checkAndPublishLowStockAlert(stockItem);

        return toDto(stockItem);
    }

    @Transactional
    public InventoryDto adjustStock(Long productId, Long storeId, int newQuantity, String reason) {
        StockItem stockItem = stockRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("StockItem",
                        String.format("productId=%d, storeId=%d", productId, storeId)));

        int previousQuantity = stockItem.getQuantity();
        int adjustment = newQuantity - previousQuantity;
        stockItem.setQuantity(newQuantity);
        stockItem = stockRepository.save(stockItem);

        recordMovement(stockItem, StockMovement.MovementType.ADJUSTMENT, adjustment, previousQuantity, reason);

        log.info("Adjusted stock for product {} at store {} from {} to {}",
                productId, storeId, previousQuantity, newQuantity);

        checkAndPublishLowStockAlert(stockItem);

        return toDto(stockItem);
    }

    @Transactional
    public InventoryDto updateThresholds(Long productId, Long storeId, Integer lowStockThreshold, Integer reorderQuantity) {
        StockItem stockItem = stockRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("StockItem",
                        String.format("productId=%d, storeId=%d", productId, storeId)));

        if (lowStockThreshold != null) {
            stockItem.setLowStockThreshold(lowStockThreshold);
        }
        if (reorderQuantity != null) {
            stockItem.setReorderQuantity(reorderQuantity);
        }

        stockItem = stockRepository.save(stockItem);
        log.info("Updated thresholds for product {} at store {}", productId, storeId);

        checkAndPublishLowStockAlert(stockItem);

        return toDto(stockItem);
    }

    private void recordMovement(StockItem stockItem, StockMovement.MovementType type,
                                 int quantity, int previousQuantity, String reason) {
        StockMovement movement = StockMovement.builder()
                .stockItem(stockItem)
                .type(type)
                .quantity(quantity)
                .previousQuantity(previousQuantity)
                .newQuantity(stockItem.getQuantity())
                .reason(reason)
                .build();
        movementRepository.save(movement);
    }

    private void checkAndPublishLowStockAlert(StockItem stockItem) {
        if (stockItem.isBelowThreshold() || stockItem.isOutOfStock()) {
            eventPublisher.publishLowStockAlert(stockItem);
        }
    }

    private InventoryDto toDto(StockItem stockItem) {
        return InventoryDto.builder()
                .id(stockItem.getId())
                .productId(stockItem.getProductId())
                .productSku(stockItem.getProductSku())
                .productName(stockItem.getProductName())
                .storeId(stockItem.getStoreId())
                .warehouseId(stockItem.getWarehouse() != null ? stockItem.getWarehouse().getId() : null)
                .quantity(stockItem.getQuantity())
                .reservedQuantity(stockItem.getReservedQuantity())
                .lowStockThreshold(stockItem.getLowStockThreshold())
                .reorderQuantity(stockItem.getReorderQuantity())
                .status(InventoryDto.StockStatus.valueOf(stockItem.getStatus().name()))
                .lastRestocked(stockItem.getLastRestocked())
                .updatedAt(stockItem.getUpdatedAt())
                .build();
    }
}