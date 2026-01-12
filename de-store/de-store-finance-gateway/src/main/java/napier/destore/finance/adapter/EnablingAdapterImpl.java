package napier.destore.finance.adapter;

import lombok.extern.slf4j.Slf4j;
import napier.destore.finance.domain.FinanceApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("prod")
@Slf4j
public class EnablingAdapterImpl implements EnablingAdapter {



    @Override
    public FinanceApplication submitApplication(FinanceApplication application) {
        log.info("Submitting to real Enabling API: {}", application.getApplicationReference());

        // TODO: Implement real API call
        // ResponseEntity<EnablingResponse> response = restTemplate.postForEntity(
        //     "https://api.enabling.com/v1/applications",
        //     buildRequest(application),
        //     EnablingResponse.class
        // );

        throw new UnsupportedOperationException("Production Enabling integration not yet implemented");
    }

    @Override
    public ApplicationStatusResponse checkStatus(String externalReference) {
        log.info("Checking status with real Enabling API: {}", externalReference);

        // TODO: Implement real API call
        throw new UnsupportedOperationException("Production Enabling integration not yet implemented");
    }
}