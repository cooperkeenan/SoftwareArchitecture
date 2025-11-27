package napier.destore.finance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.dto.FinanceApplicationDto;
import napier.destore.common.exception.ResourceNotFoundException;
import napier.destore.finance.adapter.EnablingAdapter;
import napier.destore.finance.domain.ApplicationStatus;
import napier.destore.finance.domain.FinanceApplication;
import napier.destore.finance.event.FinanceEventPublisher;
import napier.destore.finance.repository.FinanceApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinanceGatewayService {

    private final FinanceApplicationRepository applicationRepository;
    private final EnablingAdapter enablingAdapter;
    private final FinanceEventPublisher eventPublisher;

    @Transactional
    public FinanceApplicationDto createApplication(FinanceApplicationDto dto) {
        FinanceApplication application = FinanceApplication.builder()
                .storeId(dto.getStoreId())
                .customerName(dto.getCustomerName())
                .customerEmail(dto.getCustomerEmail())
                .customerPhone(dto.getCustomerPhone())
                .amount(dto.getAmount())
                .termMonths(dto.getTermMonths())
                .status(ApplicationStatus.PENDING)
                .build();

        application = applicationRepository.save(application);
        log.info("Created finance application: {} for £{}",
                application.getApplicationReference(),
                application.getAmount());

        return toDto(application);
    }

    @Transactional
    public FinanceApplicationDto submitApplication(Long id) {
        FinanceApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinanceApplication", id));

        if (application.isDecided()) {
            throw new IllegalStateException("Application already decided: " + application.getStatus());
        }

        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        log.info("Submitting application to Enabling: {}", application.getApplicationReference());

        // Call external system (async would be better in production)
        try {
            application = enablingAdapter.submitApplication(application);
            application.setDecidedAt(LocalDateTime.now());

            // Determine status from adapter response
            if (application.getMonthlyPayment() != null) {
                application.setStatus(ApplicationStatus.APPROVED);
            } else {
                application.setStatus(ApplicationStatus.DECLINED);
            }

            application = applicationRepository.save(application);

            // Publish event
            eventPublisher.publishFinanceDecision(application);

        } catch (Exception e) {
            log.error("Error submitting to Enabling: {}", e.getMessage());
            application.setStatus(ApplicationStatus.ERROR);
            application.setDecisionReason("System error: " + e.getMessage());
            application = applicationRepository.save(application);
        }

        return toDto(application);
    }

    public FinanceApplicationDto getApplication(Long id) {
        FinanceApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FinanceApplication", id));
        return toDto(application);
    }

    public FinanceApplicationDto getApplicationByReference(String reference) {
        FinanceApplication application = applicationRepository.findByApplicationReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("FinanceApplication", "reference", reference));
        return toDto(application);
    }

    public List<FinanceApplicationDto> getApplicationsForStore(Long storeId) {
        return applicationRepository.findByStoreId(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<FinanceApplicationDto> getPendingApplications() {
        return applicationRepository.findByStatus(ApplicationStatus.PENDING).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private FinanceApplicationDto toDto(FinanceApplication app) {
        return FinanceApplicationDto.builder()
                .id(app.getId())
                .applicationReference(app.getApplicationReference())
                .storeId(app.getStoreId())
                .customerName(app.getCustomerName())
                .customerEmail(app.getCustomerEmail())
                .customerPhone(app.getCustomerPhone())
                .amount(app.getAmount())
                .termMonths(app.getTermMonths())
                .monthlyPayment(app.getMonthlyPayment())
                .interestRate(app.getInterestRate())
                .status(FinanceApplicationDto.ApplicationStatus.valueOf(app.getStatus().name()))
                .decisionReason(app.getDecisionReason())
                .externalReference(app.getExternalReference())
                .submittedAt(app.getSubmittedAt())
                .decidedAt(app.getDecidedAt())
                .createdAt(app.getCreatedAt())
                .build();
    }
}