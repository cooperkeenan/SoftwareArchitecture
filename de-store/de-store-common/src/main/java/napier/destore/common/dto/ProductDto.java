package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    private Long id;

    @NotBlank(message = "Product SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    private String category;

    @NotNull(message = "Store ID is required")
    private Long storeId;

    private BigDecimal basePrice;

    private BigDecimal currentPrice;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}