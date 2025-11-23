package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreDto {

    private Long id;

    @NotBlank(message = "Store code is required")
    private String storeCode;

    @NotBlank(message = "Store name is required")
    private String name;

    private String address;

    private String city;

    private String postcode;

    private String region;

    private String managerName;

    private String managerEmail;

    private String managerPhone;

    private Long warehouseId;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}