package napier.destore.finance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import napier.destore.common.dto.ApiResponse;
import napier.destore.common.dto.FinanceApplicationDto;
import napier.destore.finance.service.FinanceGatewayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Tag(name = "Finance", description = "Buy now, pay later finance applications")
public class FinanceController {

    private final FinanceGatewayService financeService;

    @PostMapping("/applications")
    @Operation(summary = "Create a new finance application")
    public ResponseEntity<ApiResponse<FinanceApplicationDto>> createApplication(
            @Valid @RequestBody FinanceApplicationDto dto) {
        FinanceApplicationDto created = financeService.createApplication(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Finance application created successfully"));
    }

    @PostMapping("/applications/{id}/submit")
    @Operation(summary = "Submit application to Enabling finance system")
    public ResponseEntity<ApiResponse<FinanceApplicationDto>> submitApplication(
            @PathVariable("id") Long id) {
        FinanceApplicationDto submitted = financeService.submitApplication(id);
        return ResponseEntity.ok(ApiResponse.success(submitted, "Application submitted successfully"));
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApiResponse<FinanceApplicationDto>> getApplication(
            @PathVariable("id") Long id) {
        FinanceApplicationDto application = financeService.getApplication(id);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    @GetMapping("/applications/reference/{reference}")
    @Operation(summary = "Get application by reference")
    public ResponseEntity<ApiResponse<FinanceApplicationDto>> getApplicationByReference(
            @PathVariable("reference") String reference) {
        FinanceApplicationDto application = financeService.getApplicationByReference(reference);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    @GetMapping("/applications/store/{storeId}")
    @Operation(summary = "Get all applications for a store")
    public ResponseEntity<ApiResponse<List<FinanceApplicationDto>>> getApplicationsForStore(
            @PathVariable("storeId") Long storeId) {
        List<FinanceApplicationDto> applications = financeService.getApplicationsForStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/applications/pending")
    @Operation(summary = "Get all pending applications")
    public ResponseEntity<ApiResponse<List<FinanceApplicationDto>>> getPendingApplications() {
        List<FinanceApplicationDto> applications = financeService.getPendingApplications();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
}