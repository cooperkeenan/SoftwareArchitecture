package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
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
public class FinanceApplicationDto {

    private Long id;

    private String applicationReference;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    private String customerPhone;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private Integer termMonths;

    private BigDecimal monthlyPayment;

    private BigDecimal interestRate;

    private ApplicationStatus status;

    private String decisionReason;

    private String externalReference;

    private LocalDateTime submittedAt;

    private LocalDateTime decidedAt;

    private LocalDateTime createdAt;

    public enum ApplicationStatus {
        PENDING,
        SUBMITTED,
        UNDER_REVIEW,
        APPROVED,
        DECLINED,
        CANCELLED,
        ERROR
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