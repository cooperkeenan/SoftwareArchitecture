package napier.destore.inventory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import napier.destore.common.dto.ApiResponse;
import napier.destore.common.dto.InventoryDto;
import napier.destore.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock management endpoints")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @Operation(summary = "Create a new stock item")
    public ResponseEntity<ApiResponse<InventoryDto>> createStockItem(@RequestBody InventoryDto dto) {
        InventoryDto created = inventoryService.createStockItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Stock item created successfully"));
    }

    @GetMapping("/product/{productId}/store/{storeId}")
    @Operation(summary = "Get stock for a product at a store")
    public ResponseEntity<ApiResponse<InventoryDto>> getStockItem(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId) {
        InventoryDto stockItem = inventoryService.getStockItem(productId, storeId);
        return ResponseEntity.ok(ApiResponse.success(stockItem));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Get all inventory for a store")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getStoreInventory(
            @PathVariable("storeId") Long storeId) {
        List<InventoryDto> inventory = inventoryService.getStoreInventory(storeId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @GetMapping("/store/{storeId}/low-stock")
    @Operation(summary = "Get low stock items for a store")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getLowStockItems(
            @PathVariable("storeId") Long storeId) {
        List<InventoryDto> lowStock = inventoryService.getLowStockItems(storeId);
        return ResponseEntity.ok(ApiResponse.success(lowStock));
    }

    @PostMapping("/product/{productId}/store/{storeId}/add")
    @Operation(summary = "Add stock (restock)")
    public ResponseEntity<ApiResponse<InventoryDto>> addStock(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "reason", required = false, defaultValue = "Manual restock") String reason) {
        InventoryDto updated = inventoryService.addStock(productId, storeId, quantity, reason);
        return ResponseEntity.ok(ApiResponse.success(updated, "Stock added successfully"));
    }

    @PostMapping("/product/{productId}/store/{storeId}/remove")
    @Operation(summary = "Remove stock (sale or adjustment)")
    public ResponseEntity<ApiResponse<InventoryDto>> removeStock(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "reason", required = false, defaultValue = "Sale") String reason) {
        InventoryDto updated = inventoryService.removeStock(productId, storeId, quantity, reason);
        return ResponseEntity.ok(ApiResponse.success(updated, "Stock removed successfully"));
    }

    @PutMapping("/product/{productId}/store/{storeId}/adjust")
    @Operation(summary = "Adjust stock to a specific quantity")
    public ResponseEntity<ApiResponse<InventoryDto>> adjustStock(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId,
            @RequestParam("newQuantity") int newQuantity,
            @RequestParam(value = "reason", required = false, defaultValue = "Stock adjustment") String reason) {
        InventoryDto updated = inventoryService.adjustStock(productId, storeId, newQuantity, reason);
        return ResponseEntity.ok(ApiResponse.success(updated, "Stock adjusted successfully"));
    }

    @PutMapping("/product/{productId}/store/{storeId}/thresholds")
    @Operation(summary = "Update low stock thresholds")
    public ResponseEntity<ApiResponse<InventoryDto>> updateThresholds(
            @PathVariable("productId") Long productId,
            @PathVariable("storeId") Long storeId,
            @RequestParam(value = "lowStockThreshold", required = false) Integer lowStockThreshold,
            @RequestParam(value = "reorderQuantity", required = false) Integer reorderQuantity) {
        InventoryDto updated = inventoryService.updateThresholds(productId, storeId, lowStockThreshold, reorderQuantity);
        return ResponseEntity.ok(ApiResponse.success(updated, "Thresholds updated successfully"));
    }
}