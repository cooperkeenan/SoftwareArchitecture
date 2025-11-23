package napier.destore.price.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import napier.destore.common.dto.ApiResponse;
import napier.destore.common.dto.PriceDto;
import napier.destore.common.dto.PromotionDto;
import napier.destore.price.service.PriceService;
import napier.destore.price.service.PromotionService;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "Price and promotion management endpoints")
public class PriceController {

    private final PriceService priceService;
    private final PromotionService promotionService;

    // Price Endpoints

    @PutMapping("/product/{productId}/store/{storeId}")
    @Operation(summary = "Set price for a product at a store")
    public ResponseEntity<ApiResponse<PriceDto>> setPrice(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId,
            @RequestParam("price") BigDecimal price) {
        PriceDto priceDto = priceService.setPrice(productId, storeId, price);
        return ResponseEntity.ok(ApiResponse.success(priceDto, "Price set successfully"));
    }

    @GetMapping("/product/{productId}/store/{storeId}")
    @Operation(summary = "Get price for a product at a store")
    public ResponseEntity<ApiResponse<PriceDto>> getPrice(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId) {
        PriceDto priceDto = priceService.getPrice(productId, storeId);
        return ResponseEntity.ok(ApiResponse.success(priceDto));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get all prices for a store")
    public ResponseEntity<ApiResponse<List<PriceDto>>> getPricesForStore(
            @PathVariable("storeId") Long storeId) {
        List<PriceDto> prices = priceService.getPricesForStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(prices));
    }

    // Promotion Endpoints

    @PostMapping("/promotions")
    @Operation(summary = "Create a new promotion")
    public ResponseEntity<ApiResponse<PromotionDto>> createPromotion(@RequestBody PromotionDto dto) {
        PromotionDto created = promotionService.createPromotion(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Promotion created successfully"));
    }

    @GetMapping("/promotions")
    @Operation(summary = "Get all promotions")
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getAllPromotions() {
        List<PromotionDto> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @GetMapping("/promotions/{id}")
    @Operation(summary = "Get promotion by ID")
    public ResponseEntity<ApiResponse<PromotionDto>> getPromotion(
            @PathVariable("id") Long id) {
        PromotionDto promotion = promotionService.getPromotion(id);
        return ResponseEntity.ok(ApiResponse.success(promotion));
    }

    @GetMapping("/promotions/store/{storeId}/active")
    @Operation(summary = "Get active promotions for a store")
    public ResponseEntity<ApiResponse<List<PromotionDto>>> getActivePromotions(
            @PathVariable("storeId") Long storeId) {
        List<PromotionDto> promotions = promotionService.getActivePromotionsForStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @DeleteMapping("/promotions/{id}")
    @Operation(summary = "Delete a promotion")
    public ResponseEntity<ApiResponse<Void>> deletePromotion(
            @PathVariable("id") Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promotion deleted successfully"));
    }


    // Apply/Remove Promotions 

    @PostMapping("/product/{productId}/store/{storeId}/promotion/{promotionId}")
    @Operation(summary = "Apply a promotion to a product at a store")
    public ResponseEntity<ApiResponse<PriceDto>> applyPromotion(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId,
            @PathVariable("promotionId") Long promotionId) {
        PriceDto priceDto = priceService.applyPromotion(productId, storeId, promotionId);
        return ResponseEntity.ok(ApiResponse.success(priceDto, "Promotion applied successfully"));
    }

    @DeleteMapping("/product/{productId}/store/{storeId}/promotion")
    @Operation(summary = "Remove promotion from a product at a store")
    public ResponseEntity<ApiResponse<PriceDto>> removePromotion(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId) {
        PriceDto priceDto = priceService.removePromotion(productId, storeId);
        return ResponseEntity.ok(ApiResponse.success(priceDto, "Promotion removed successfully"));
    }
}