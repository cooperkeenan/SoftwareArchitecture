package napier.destore.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PriceChangeEvent extends BaseEvent {

    public static final String EVENT_TYPE = "price.changed";
    public static final String SOURCE = "price-service";

    private Long productId;
    private String productSku;
    private String productName;
    private BigDecimal previousPrice;
    private BigDecimal newPrice;
    private BigDecimal changeAmount;
    private Double changePercentage;
    private String reason;
    private Long promotionId;
    private String changedBy;

    public static PriceChangeEvent create(Long storeId, Long productId, String productSku,
                                           String productName, BigDecimal previousPrice,
                                           BigDecimal newPrice, String reason) {
        BigDecimal change = newPrice.subtract(previousPrice);
        double percentage = previousPrice.compareTo(BigDecimal.ZERO) != 0
                ? change.doubleValue() / previousPrice.doubleValue() * 100
                : 0.0;

        PriceChangeEvent event = PriceChangeEvent.builder()
                .storeId(storeId)
                .productId(productId)
                .productSku(productSku)
                .productName(productName)
                .previousPrice(previousPrice)
                .newPrice(newPrice)
                .changeAmount(change)
                .changePercentage(percentage)
                .reason(reason)
                .build();
        event.initializeDefaults(SOURCE, EVENT_TYPE);
        return event;
    }

    public boolean isPriceIncrease() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isPriceDecrease() {
        return changeAmount != null && changeAmount.compareTo(BigDecimal.ZERO) < 0;
    }
}