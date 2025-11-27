package napier.destore.finance.adapter;

import napier.destore.finance.domain.FinanceApplication;

/**
 * Adapter interface for the Enabling finance system.
 * Follows Adapter Pattern and Dependency Inversion Principle.
 * 
 * In production, EnablingAdapterImpl would make real API calls.
 * For prototype, EnablingStub simulates the external system.
 */
public interface EnablingAdapter {

    /**
     * Submit a finance application to the Enabling system.
     * @param application The application to submit
     * @return The application with updated status and external reference
     */
    FinanceApplication submitApplication(FinanceApplication application);

    /**
     * Check the status of a previously submitted application.
     * @param externalReference The reference from Enabling system
     * @return Updated application status
     */
    ApplicationStatusResponse checkStatus(String externalReference);

    /**
     * Response from Enabling system.
     */
    record ApplicationStatusResponse(
            String externalReference,
            boolean approved,
            String reason,
            Integer termMonths,
            java.math.BigDecimal monthlyPayment,
            java.math.BigDecimal interestRate
    ) {}
}