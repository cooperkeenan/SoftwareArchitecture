package napier.destore.price.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.dto.PromotionDto;
import napier.destore.common.exception.ResourceNotFoundException;
import napier.destore.price.domain.Promotion;
import napier.destore.price.domain.PromotionType;
import napier.destore.price.repository.PromotionRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final PromotionRepository promotionRepository;

    @Transactional
    public PromotionDto createPromotion(PromotionDto dto) {
        Promotion promotion = Promotion.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .type(toEntityType(dto.getType()))
                .storeId(dto.getStoreId())
                .discountPercentage(dto.getDiscountPercentage())
                .discountAmount(dto.getDiscountAmount())
                .buyQuantity(dto.getBuyQuantity())
                .getQuantity(dto.getGetQuantity())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        promotion = promotionRepository.save(promotion);
        log.info("Created promotion: {} ({})", promotion.getName(), promotion.getType());

        return toDto(promotion);
    }

    public PromotionDto getPromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", id));
        return toDto(promotion);
    }

    public List<PromotionDto> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PromotionDto> getActivePromotionsForStore(Long storeId) {
        return promotionRepository.findActivePromotionsForStore(storeId, LocalDateTime.now()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromotionDto updatePromotion(Long id, PromotionDto dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", id));

        if (dto.getName() != null) promotion.setName(dto.getName());
        if (dto.getDescription() != null) promotion.setDescription(dto.getDescription());
        if (dto.getActive() != null) promotion.setActive(dto.getActive());
        if (dto.getStartDate() != null) promotion.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) promotion.setEndDate(dto.getEndDate());

        promotion = promotionRepository.save(promotion);
        log.info("Updated promotion: {}", promotion.getId());

        return toDto(promotion);
    }

    @Transactional
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion", id);
        }
        promotionRepository.deleteById(id);
        log.info("Deleted promotion: {}", id);
    }

    private PromotionDto toDto(Promotion promotion) {
        return PromotionDto.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .type(toDtoType(promotion.getType()))
                .storeId(promotion.getStoreId())
                .discountPercentage(promotion.getDiscountPercentage())
                .discountAmount(promotion.getDiscountAmount())
                .buyQuantity(promotion.getBuyQuantity())
                .getQuantity(promotion.getGetQuantity())
                .active(promotion.getActive())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .createdAt(promotion.getCreatedAt())
                .build();
    }

    private PromotionType toEntityType(PromotionDto.PromotionType dtoType) {
        return PromotionType.valueOf(dtoType.name());
    }

    private PromotionDto.PromotionType toDtoType(PromotionType entityType) {
        return PromotionDto.PromotionType.valueOf(entityType.name());
    }
}