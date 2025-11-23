package napier.destore.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LowStockEvent extends BaseEvent {

    public static final String EVENT_TYPE = "inventory.low-stock";
    public static final String SOURCE = "inventory-service";

    private Long productId;
    private String productSku;
    private String productName;
    private Integer currentQuantity;
    private Integer threshold;
    private Integer reorderQuantity;
    private Long warehouseId;
    private boolean outOfStock;

    public static LowStockEvent create(Long storeId, Long productId, String productSku,
                                        String productName, int currentQuantity, int threshold) {
        LowStockEvent event = LowStockEvent.builder()
                .storeId(storeId)
                .productId(productId)
                .productSku(productSku)
                .productName(productName)
                .currentQuantity(currentQuantity)
                .threshold(threshold)
                .outOfStock(currentQuantity <= 0)
                .build();
        event.initializeDefaults(SOURCE, EVENT_TYPE);
        return event;
    }

    public String getAlertMessage() {
        if (outOfStock) {
            return String.format("OUT OF STOCK: %s (SKU: %s) is out of stock at store %d",
                    productName, productSku, getStoreId());
        }
        return String.format("LOW STOCK: %s (SKU: %s) has %d units remaining (threshold: %d) at store %d",
                productName, productSku, currentQuantity, threshold, getStoreId());
    }
}