package napier.destore.finance.adapter;

import napier.destore.finance.domain.FinanceApplication;


//Adapter interface for the Enabling finance system.
public interface EnablingAdapter {

    FinanceApplication submitApplication(FinanceApplication application);

    ApplicationStatusResponse checkStatus(String externalReference);

    record ApplicationStatusResponse(
            String externalReference,
            boolean approved,
            String reason,
            Integer termMonths,
            java.math.BigDecimal monthlyPayment,
            java.math.BigDecimal interestRate
    ) {}
}