package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryDto {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productSku;

    private String productName;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    private Long warehouseId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Reserved quantity cannot be negative")
    private Integer reservedQuantity;

    private Integer lowStockThreshold;

    private Integer reorderQuantity;

    private StockStatus status;

    private LocalDateTime lastRestocked;

    private LocalDateTime updatedAt;

    public enum StockStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK,
        ON_ORDER
    }

    public int getAvailableQuantity() {
        int qty = quantity != null ? quantity : 0;
        int reserved = reservedQuantity != null ? reservedQuantity : 0;
        return Math.max(0, qty - reserved);
    }

    public boolean isBelowThreshold() {
        if (lowStockThreshold == null) {
            return false;
        }
        return getAvailableQuantity() < lowStockThreshold;
    }
}