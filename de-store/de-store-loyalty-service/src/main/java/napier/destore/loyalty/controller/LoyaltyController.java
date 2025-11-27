package napier.destore.loyalty.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import napier.destore.common.dto.ApiResponse;
import napier.destore.common.dto.LoyaltyCardDto;
import napier.destore.loyalty.domain.PointsTransaction;
import napier.destore.loyalty.service.LoyaltyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
@Tag(name = "Loyalty", description = "Customer loyalty program management")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @PostMapping("/cards")
    @Operation(summary = "Create a new loyalty card")
    public ResponseEntity<ApiResponse<LoyaltyCardDto>> createCard(@Valid @RequestBody LoyaltyCardDto dto) {
        LoyaltyCardDto created = loyaltyService.createCard(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Loyalty card created successfully"));
    }

    @GetMapping("/cards/{id}")
    @Operation(summary = "Get loyalty card by ID")
    public ResponseEntity<ApiResponse<LoyaltyCardDto>> getCard(@PathVariable("id") Long id) {
        LoyaltyCardDto card = loyaltyService.getCard(id);
        return ResponseEntity.ok(ApiResponse.success(card));
    }

    @GetMapping("/cards/number/{cardNumber}")
    @Operation(summary = "Get loyalty card by card number")
    public ResponseEntity<ApiResponse<LoyaltyCardDto>> getCardByNumber(
            @PathVariable("cardNumber") String cardNumber) {
        LoyaltyCardDto card = loyaltyService.getCardByNumber(cardNumber);
        return ResponseEntity.ok(ApiResponse.success(card));
    }

    @GetMapping("/cards/email/{email}")
    @Operation(summary = "Get loyalty card by customer email")
    public ResponseEntity<ApiResponse<LoyaltyCardDto>> getCardByEmail(
            @PathVariable("email") String email) {
        LoyaltyCardDto card = loyaltyService.getCardByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(card));
    }

    @GetMapping("/cards")
    @Operation(summary = "Get all loyalty cards")
    public ResponseEntity<ApiResponse<List<LoyaltyCardDto>>> getAllCards() {
        List<LoyaltyCardDto> cards = loyaltyService.getAllCards();
        return ResponseEntity.ok(ApiResponse.success(cards));
    }

    @PostMapping("/cards/{cardNumber}/award")
    @Operation(summary = "Award points for a purchase")
    public ResponseEntity<ApiResponse<LoyaltyCardDto>> awardPoints(
            @PathVariable("cardNumber") String cardNumber,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "storeId", required = false) Long storeId,
            @RequestParam(value = "description", required = false) String description) {
        LoyaltyCardDto updated = loyaltyService.awardPoints(cardNumber, amount, storeId, description);
        return ResponseEntity.ok(ApiResponse.success(updated, "Points awarded successfully"));
    }

    @PostMapping("/cards/{cardNumber}/redeem")
    @Operation(summary = "Redeem points")
    public ResponseEntity<ApiResponse<LoyaltyCardDto>> redeemPoints(
            @PathVariable("cardNumber") String cardNumber,
            @RequestParam("points") int points,
            @RequestParam(value = "description", required = false) String description) {
        LoyaltyCardDto updated = loyaltyService.redeemPoints(cardNumber, points, description);
        return ResponseEntity.ok(ApiResponse.success(updated, "Points redeemed successfully"));
    }

    @GetMapping("/cards/{cardNumber}/transactions")
    @Operation(summary = "Get transaction history for a card")
    public ResponseEntity<ApiResponse<List<PointsTransaction>>> getTransactionHistory(
            @PathVariable("cardNumber") String cardNumber) {
        List<PointsTransaction> transactions = loyaltyService.getTransactionHistory(cardNumber);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
}