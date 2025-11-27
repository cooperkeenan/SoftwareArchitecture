package napier.destore.loyalty.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loyalty_card_id", nullable = false)
    private LoyaltyCard loyaltyCard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "previous_balance")
    private Integer previousBalance;

    @Column(name = "new_balance")
    private Integer newBalance;

    @Column(name = "purchase_amount", precision = 10, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(name = "store_id")
    private Long storeId;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum TransactionType {
        EARNED,
        REDEEMED,
        ADJUSTMENT,
        EXPIRED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}