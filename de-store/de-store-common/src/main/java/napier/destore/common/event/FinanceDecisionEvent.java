package napier.destore.common.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import napier.destore.common.dto.FinanceApplicationDto;

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
    private FinanceApplicationDto.ApplicationStatus status;
    private String decisionReason;
    private BigDecimal approvedAmount;
    private Integer termMonths;
    private BigDecimal monthlyPayment;
    private String externalReference;

    public static FinanceDecisionEvent create(
            Long storeId,
            Long applicationId,
            String applicationReference,
            String customerName,
            String customerEmail,
            BigDecimal amount,
            FinanceApplicationDto.ApplicationStatus status,
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

    @JsonIgnore
    public boolean isApproved() {
        return status == FinanceApplicationDto.ApplicationStatus.APPROVED;
    }

    @JsonIgnore
    public String getDecisionMessage() {
        String statusText = isApproved() ? "APPROVED" : "DECLINED";
        StringBuilder message = new StringBuilder();
        message.append(String.format("Finance application %s for %s (%s) has been %s.",
                applicationReference, customerName, customerEmail, statusText));

        if (isApproved() && monthlyPayment != null) {
            message.append(String.format(" Amount: £%s over %d months at £%s/month",
                    amount, termMonths, monthlyPayment));
            
            // Only show interest rate if we have it (not the approved amount)
            if (externalReference != null) {
                message.append(" (5.9% APR)");  // Fixed rate from stub
            }
            message.append(".");
        }

        if (decisionReason != null) {
            message.append(" Reason: ").append(decisionReason);
        }

        return message.toString();
    }
}