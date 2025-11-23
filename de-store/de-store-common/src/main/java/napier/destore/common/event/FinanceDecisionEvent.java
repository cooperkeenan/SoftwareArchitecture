package napier.destore.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import napier.destore.common.dto.FinanceApplicationDto.ApplicationStatus;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FinanceDecisionEvent extends BaseEvent {

    public static final String EVENT_TYPE = "finance.decision";
    public static final String SOURCE = "finance-gateway-service";

    private Long applicationId;
    private String applicationReference;
    private String customerName;
    private String customerEmail;
    private BigDecimal amount;
    private ApplicationStatus status;
    private String decisionReason;
    private BigDecimal approvedAmount;
    private Integer termMonths;
    private BigDecimal monthlyPayment;
    private String externalReference;

    public static FinanceDecisionEvent create(Long storeId, Long applicationId,
                                               String applicationReference,
                                               String customerName, String customerEmail,
                                               BigDecimal amount, ApplicationStatus status,
                                               String decisionReason) {
        FinanceDecisionEvent event = FinanceDecisionEvent.builder()
                .storeId(storeId)
                .applicationId(applicationId)
                .applicationReference(applicationReference)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .amount(amount)
                .status(status)
                .decisionReason(decisionReason)
                .build();
        event.initializeDefaults(SOURCE, EVENT_TYPE);
        return event;
    }

    public boolean isApproved() {
        return status == ApplicationStatus.APPROVED;
    }

    public boolean isDeclined() {
        return status == ApplicationStatus.DECLINED;
    }

    public String getDecisionMessage() {
        if (isApproved()) {
            return String.format("Finance APPROVED for %s - £%.2f over %d months",
                    customerName, amount, termMonths != null ? termMonths : 0);
        } else if (isDeclined()) {
            return String.format("Finance DECLINED for %s - £%.2f. Reason: %s",
                    customerName, amount, decisionReason != null ? decisionReason : "Not specified");
        }
        return String.format("Finance application %s for %s - £%.2f",
                status, customerName, amount);
    }
}