package napier.destore.finance.adapter;

import lombok.extern.slf4j.Slf4j;
import napier.destore.finance.domain.FinanceApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Stub implementation of Enabling finance system.
 * Simulates external API calls with random decisions.
 * Active for development/demo profiles.
 */
@Component
@Profile({"default", "dev", "demo"})
@Slf4j
public class EnablingStub implements EnablingAdapter {

    private final Random random = new Random();

    @Value("${enabling.auto-approve-threshold:1000}")
    private BigDecimal autoApproveThreshold;

    @Value("${enabling.approval-rate:0.7}")
    private double approvalRate;

    @Override
    public FinanceApplication submitApplication(FinanceApplication application) {
        log.info("📤 [STUB] Submitting to Enabling: {} for £{}",
                application.getApplicationReference(),
                application.getAmount());

        // Simulate external system processing delay
        simulateDelay();

        // Generate external reference
        String externalRef = "ENA-" + System.currentTimeMillis();

        // Decision logic
        boolean approved = shouldApprove(application.getAmount());

        if (approved) {
            log.info("✅ [STUB] Enabling APPROVED: {}", application.getApplicationReference());
            return approveApplication(application, externalRef);
        } else {
            log.info("❌ [STUB] Enabling DECLINED: {}", application.getApplicationReference());
            return declineApplication(application, externalRef);
        }
    }

    @Override
    public ApplicationStatusResponse checkStatus(String externalReference) {
        log.info("🔍 [STUB] Checking status for: {}", externalReference);
        
        // Simulate check - return dummy data
        return new ApplicationStatusResponse(
                externalReference,
                true,
                "Application processed",
                12,
                new BigDecimal("100.00"),
                new BigDecimal("5.9")
        );
    }

    private boolean shouldApprove(BigDecimal amount) {
        // Auto-approve small amounts
        if (amount.compareTo(autoApproveThreshold) <= 0) {
            return true;
        }

        // Random approval based on configured rate
        return random.nextDouble() < approvalRate;
    }

    private FinanceApplication approveApplication(FinanceApplication app, String externalRef) {
        app.setExternalReference(externalRef);

        // Default to 12 months if not specified
        int months = app.getTermMonths() != null ? app.getTermMonths() : 12;
        app.setTermMonths(months);

        // Calculate monthly payment (simplified - no compounding)
        BigDecimal interestRate = new BigDecimal("5.9"); // 5.9% APR
        app.setInterestRate(interestRate);

        BigDecimal totalWithInterest = app.getAmount()
                .multiply(BigDecimal.ONE.add(interestRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
        BigDecimal monthlyPayment = totalWithInterest.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        app.setMonthlyPayment(monthlyPayment);

        app.setDecisionReason("Application meets lending criteria");

        return app;
    }

    private FinanceApplication declineApplication(FinanceApplication app, String externalRef) {
        app.setExternalReference(externalRef);

        String[] reasons = {
                "Credit score below threshold",
                "Insufficient income verification",
                "Existing debt levels too high",
                "Unable to verify employment"
        };

        app.setDecisionReason(reasons[random.nextInt(reasons.length)]);

        return app;
    }

    private void simulateDelay() {
        try {
            // Simulate network call (500ms - 1.5s)
            Thread.sleep(500 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}