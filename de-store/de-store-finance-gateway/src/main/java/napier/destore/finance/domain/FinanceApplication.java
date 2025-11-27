package napier.destore.finance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_reference", unique = true)
    private String applicationReference;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "term_months")
    private Integer termMonths;

    @Column(name = "monthly_payment", precision = 10, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(name = "decision_reason")
    private String decisionReason;

    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
        if (applicationReference == null) {
            applicationReference = generateReference();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateReference() {
        return "FIN-" + System.currentTimeMillis();
    }

    public boolean isDecided() {
        return status == ApplicationStatus.APPROVED ||
               status == ApplicationStatus.DECLINED ||
               status == ApplicationStatus.CANCELLED;
    }

    public boolean isPending() {
        return status == ApplicationStatus.PENDING ||
               status == ApplicationStatus.SUBMITTED ||
               status == ApplicationStatus.UNDER_REVIEW;
    }
}