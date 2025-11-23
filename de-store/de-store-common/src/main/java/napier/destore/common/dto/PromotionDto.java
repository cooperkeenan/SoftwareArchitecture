package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PromotionDto {

    private Long id;

    @NotBlank(message = "Promotion name is required")
    private String name;

    private String description;

    @NotNull(message = "Promotion type is required")
    private PromotionType type;

    private Long storeId;

    private BigDecimal discountPercentage;

    private BigDecimal discountAmount;

    private Integer buyQuantity;

    private Integer getQuantity;

    private Boolean freeDelivery;

    private Boolean active;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime createdAt;

    public enum PromotionType {
        PERCENTAGE_DISCOUNT,
        FIXED_DISCOUNT,
        BUY_X_GET_Y_FREE,
        THREE_FOR_TWO,
        BUY_ONE_GET_ONE_FREE,
        FREE_DELIVERY
    }

    public boolean isCurrentlyActive() {
        if (!Boolean.TRUE.equals(active)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        boolean afterStart = startDate == null || now.isAfter(startDate);
        boolean beforeEnd = endDate == null || now.isBefore(endDate);
        return afterStart && beforeEnd;
    }
}