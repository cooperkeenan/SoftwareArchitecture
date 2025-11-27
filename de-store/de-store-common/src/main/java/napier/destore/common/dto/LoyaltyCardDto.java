package napier.destore.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyCardDto {

    private Long id;
    private String cardNumber;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Integer pointsBalance;
    private LoyaltyTier tier;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime tierUpdatedAt;
    
    private Boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public enum LoyaltyTier {
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM
    }
}