package napier.destore.loyalty.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "points_balance", nullable = false)
    private Integer pointsBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoyaltyTier tier;

    @Column(name = "tier_updated_at")
    private LocalDateTime tierUpdatedAt;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum LoyaltyTier {
        BRONZE(0),
        SILVER(1000),
        GOLD(5000),
        PLATINUM(10000);

        private final int threshold;

        LoyaltyTier(int threshold) {
            this.threshold = threshold;
        }

        public int getThreshold() {
            return threshold;
        }

        public static LoyaltyTier fromPoints(int points) {
            if (points >= PLATINUM.threshold) return PLATINUM;
            if (points >= GOLD.threshold) return GOLD;
            if (points >= SILVER.threshold) return SILVER;
            return BRONZE;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (pointsBalance == null) pointsBalance = 0;
        if (active == null) active = true;
        if (tier == null) tier = LoyaltyTier.BRONZE;
        if (cardNumber == null) cardNumber = generateCardNumber();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateCardNumber() {
        return "LOY-" + System.currentTimeMillis();
    }

    public void addPoints(int points) {
        this.pointsBalance += points;
        updateTier();
    }

    public void deductPoints(int points) {
        this.pointsBalance = Math.max(0, this.pointsBalance - points);
        updateTier();
    }

    private void updateTier() {
        LoyaltyTier newTier = LoyaltyTier.fromPoints(pointsBalance);
        if (newTier != this.tier) {
            this.tier = newTier;
            this.tierUpdatedAt = LocalDateTime.now();
        }
    }
}