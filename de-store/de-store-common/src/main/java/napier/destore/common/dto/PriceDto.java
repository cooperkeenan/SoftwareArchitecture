package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceDto {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productSku;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    private BigDecimal discountedPrice;

    private BigDecimal finalPrice;

    private PromotionDto activePromotion;

    private LocalDateTime effectiveFrom;

    private LocalDateTime effectiveTo;

    private LocalDateTime updatedAt;

    public BigDecimal getFinalPrice() {
        if (finalPrice != null) {
            return finalPrice;
        }
        return discountedPrice != null ? discountedPrice : basePrice;
    }
}