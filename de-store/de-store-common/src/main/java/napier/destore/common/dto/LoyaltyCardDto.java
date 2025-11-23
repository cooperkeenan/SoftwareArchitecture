package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class LoyaltyCardDto {

    private Long id;

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Email(message = "Valid email is required")
    private String email;

    private String phoneNumber;

    private Long preferredStoreId;

    private Integer points;

    private BigDecimal totalSpent;

    private LoyaltyTier tier;

    private Boolean active;

    private LocalDateTime joinedAt;

    private LocalDateTime lastPurchaseAt;

    public enum LoyaltyTier {
        BRONZE(0, 0.01),
        SILVER(500, 0.02),
        GOLD(2000, 0.03),
        PLATINUM(5000, 0.05);

        private final int pointsRequired;
        private final double discountRate;

        LoyaltyTier(int pointsRequired, double discountRate) {
            this.pointsRequired = pointsRequired;
            this.discountRate = discountRate;
        }

        public int getPointsRequired() {
            return pointsRequired;
        }

        public double getDiscountRate() {
            return discountRate;
        }

        public static LoyaltyTier fromPoints(int points) {
            LoyaltyTier result = BRONZE;
            for (LoyaltyTier tier : values()) {
                if (points >= tier.pointsRequired) {
                    result = tier;
                }
            }
            return result;
        }
    }
}