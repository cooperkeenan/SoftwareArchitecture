package napier.destore.loyalty.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.dto.LoyaltyCardDto;
import napier.destore.common.exception.ResourceNotFoundException;
import napier.destore.common.exception.ValidationException;
import napier.destore.loyalty.domain.LoyaltyCard;
import napier.destore.loyalty.domain.PointsTransaction;
import napier.destore.loyalty.repository.LoyaltyCardRepository;
import napier.destore.loyalty.repository.PointsTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final LoyaltyCardRepository loyaltyCardRepository;
    private final PointsTransactionRepository transactionRepository;

    @Value("${loyalty.points-per-pound:10}")
    private int pointsPerPound;

    @Transactional
    public LoyaltyCardDto createCard(LoyaltyCardDto dto) {
        if (loyaltyCardRepository.existsByCustomerEmail(dto.getCustomerEmail())) {
            throw new ValidationException("customerEmail", "Loyalty card already exists for this email");
        }

        LoyaltyCard card = LoyaltyCard.builder()
                .customerName(dto.getCustomerName())
                .customerEmail(dto.getCustomerEmail())
                .customerPhone(dto.getCustomerPhone())
                .pointsBalance(0)
                .tier(LoyaltyCard.LoyaltyTier.BRONZE)
                .active(true)
                .build();

        card = loyaltyCardRepository.save(card);
        log.info("Created loyalty card: {} for {}", card.getCardNumber(), card.getCustomerName());

        return toDto(card);
    }

    public LoyaltyCardDto getCard(Long id) {
        LoyaltyCard card = loyaltyCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyCard", id));
        return toDto(card);
    }

    public LoyaltyCardDto getCardByNumber(String cardNumber) {
        LoyaltyCard card = loyaltyCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyCard", "cardNumber", cardNumber));
        return toDto(card);
    }

    public LoyaltyCardDto getCardByEmail(String email) {
        LoyaltyCard card = loyaltyCardRepository.findByCustomerEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyCard", "email", email));
        return toDto(card);
    }

    public List<LoyaltyCardDto> getAllCards() {
        return loyaltyCardRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoyaltyCardDto awardPoints(String cardNumber, BigDecimal purchaseAmount, Long storeId, String description) {
        LoyaltyCard card = loyaltyCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyCard", "cardNumber", cardNumber));

        int pointsToAward = calculatePoints(purchaseAmount);
        int previousBalance = card.getPointsBalance();

        card.addPoints(pointsToAward);
        card = loyaltyCardRepository.save(card);

        // Record transaction
        PointsTransaction transaction = PointsTransaction.builder()
                .loyaltyCard(card)
                .type(PointsTransaction.TransactionType.EARNED)
                .points(pointsToAward)
                .previousBalance(previousBalance)
                .newBalance(card.getPointsBalance())
                .purchaseAmount(purchaseAmount)
                .storeId(storeId)
                .description(description != null ? description : "Purchase")
                .build();
        transactionRepository.save(transaction);

        log.info("Awarded {} points to card {} (purchase: £{}). New balance: {}",
                pointsToAward, cardNumber, purchaseAmount, card.getPointsBalance());

        return toDto(card);
    }

    @Transactional
    public LoyaltyCardDto redeemPoints(String cardNumber, int points, String description) {
        LoyaltyCard card = loyaltyCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyCard", "cardNumber", cardNumber));

        if (card.getPointsBalance() < points) {
            throw new ValidationException("Insufficient points. Available: " + card.getPointsBalance());
        }

        int previousBalance = card.getPointsBalance();
        card.deductPoints(points);
        card = loyaltyCardRepository.save(card);

        // Record transaction
        PointsTransaction transaction = PointsTransaction.builder()
                .loyaltyCard(card)
                .type(PointsTransaction.TransactionType.REDEEMED)
                .points(-points)
                .previousBalance(previousBalance)
                .newBalance(card.getPointsBalance())
                .description(description != null ? description : "Points redeemed")
                .build();
        transactionRepository.save(transaction);

        log.info("Redeemed {} points from card {}. New balance: {}",
                points, cardNumber, card.getPointsBalance());

        return toDto(card);
    }

    public List<PointsTransaction> getTransactionHistory(String cardNumber) {
        LoyaltyCard card = loyaltyCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyCard", "cardNumber", cardNumber));
        return transactionRepository.findByLoyaltyCardId(card.getId());
    }

    private int calculatePoints(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(pointsPerPound)).intValue();
    }

    private LoyaltyCardDto toDto(LoyaltyCard card) {
        return LoyaltyCardDto.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .customerName(card.getCustomerName())
                .customerEmail(card.getCustomerEmail())
                .customerPhone(card.getCustomerPhone())
                .pointsBalance(card.getPointsBalance())
                .tier(LoyaltyCardDto.LoyaltyTier.valueOf(card.getTier().name()))
                .tierUpdatedAt(card.getTierUpdatedAt())
                .active(card.getActive())
                .createdAt(card.getCreatedAt())
                .build();
    }
}