package napier.destore.price.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import napier.destore.common.dto.PriceDto;
import napier.destore.common.dto.ProductDto;
import napier.destore.common.exception.ResourceNotFoundException;
import napier.destore.common.exception.ValidationException;
import napier.destore.price.domain.Product;
import napier.destore.price.domain.Promotion;
import napier.destore.price.domain.StorePrice;
import napier.destore.price.event.PriceEventPublisher;
import napier.destore.price.repository.ProductRepository;
import napier.destore.price.repository.PromotionRepository;
import napier.destore.price.repository.StorePriceRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    private final ProductRepository productRepository;
    private final StorePriceRepository storePriceRepository;
    private final PromotionRepository promotionRepository;
    private final PriceEventPublisher eventPublisher;


    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new ValidationException("sku", "Product with SKU already exists: " + dto.getSku());
        }

        Product product = Product.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        product = productRepository.save(product);
        log.info("Created product: {} (SKU: {})", product.getName(), product.getSku());

        return toProductDto(product);
    }

    public ProductDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return toProductDto(product);
    }

    public ProductDto getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return toProductDto(product);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toProductDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getActive() != null) product.setActive(dto.getActive());

        product = productRepository.save(product);
        log.info("Updated product: {}", product.getId());

        return toProductDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        storePriceRepository.deleteByProductId(id);
        productRepository.deleteById(id);
        log.info("Deleted product: {}", id);
    }


    @Transactional
    public PriceDto setPrice(Long productId, Long storeId, BigDecimal price) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        StorePrice storePrice = storePriceRepository.findByProductIdAndStoreId(productId, storeId)
                .orElse(StorePrice.builder()
                        .product(product)
                        .storeId(storeId)
                        .build());

        BigDecimal oldPrice = storePrice.getBasePrice();
        storePrice.setBasePrice(price);
        storePrice = storePriceRepository.save(storePrice);

        log.info("Set price for product {} at store {}: £{}", productId, storeId, price);

        // Publish price change event
        if (oldPrice != null && oldPrice.compareTo(price) != 0) {
            eventPublisher.publishPriceChange(storeId, product, oldPrice, price, "Manual price update");
        }

        return toPriceDto(storePrice);
    }

    public PriceDto getPrice(Long productId, Long storeId) {
        StorePrice storePrice = storePriceRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Price", 
                    String.format("productId=%d, storeId=%d", productId, storeId)));
        return toPriceDto(storePrice);
    }

    public List<PriceDto> getPricesForStore(Long storeId) {
        return storePriceRepository.findByStoreId(storeId).stream()
                .map(this::toPriceDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public PriceDto applyPromotion(Long productId, Long storeId, Long promotionId) {
        StorePrice storePrice = storePriceRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Price",
                    String.format("productId=%d, storeId=%d", productId, storeId)));

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", promotionId));

        BigDecimal oldPrice = storePrice.getCurrentPrice();
        storePrice.setActivePromotion(promotion);
        storePrice.setDiscountedPrice(calculateDiscountedPrice(storePrice.getBasePrice(), promotion));
        storePrice = storePriceRepository.save(storePrice);

        BigDecimal newPrice = storePrice.getCurrentPrice();
        log.info("Applied promotion {} to product {} at store {}: £{} -> £{}", 
                promotionId, productId, storeId, oldPrice, newPrice);

        // Publish price change event
        eventPublisher.publishPriceChange(storeId, storePrice.getProduct(), oldPrice, newPrice,
                "Promotion applied: " + promotion.getName());

        return toPriceDto(storePrice);
    }

    @Transactional
    public PriceDto removePromotion(Long productId, Long storeId) {
        StorePrice storePrice = storePriceRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Price",
                    String.format("productId=%d, storeId=%d", productId, storeId)));

        BigDecimal oldPrice = storePrice.getCurrentPrice();
        String promotionName = storePrice.getActivePromotion() != null ? 
                storePrice.getActivePromotion().getName() : "Unknown";

        storePrice.setActivePromotion(null);
        storePrice.setDiscountedPrice(null);
        storePrice = storePriceRepository.save(storePrice);

        log.info("Removed promotion from product {} at store {}", productId, storeId);

        // Publish price change event
        eventPublisher.publishPriceChange(storeId, storePrice.getProduct(), oldPrice, 
                storePrice.getBasePrice(), "Promotion removed: " + promotionName);

        return toPriceDto(storePrice);
    }


    private BigDecimal calculateDiscountedPrice(BigDecimal basePrice, Promotion promotion) {
        if (promotion == null || basePrice == null) {
            return basePrice;
        }

        return switch (promotion.getType()) {
            case PERCENTAGE_DISCOUNT -> {
                BigDecimal discount = basePrice.multiply(promotion.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                yield basePrice.subtract(discount);
            }
            case FIXED_DISCOUNT -> basePrice.subtract(promotion.getDiscountAmount()).max(BigDecimal.ZERO);
            case BUY_ONE_GET_ONE_FREE -> basePrice.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            case THREE_FOR_TWO -> basePrice.multiply(BigDecimal.valueOf(2))
                    .divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
            case FREE_DELIVERY -> basePrice; // Price doesn't change, delivery is free
        };
    }

    private ProductDto toProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private PriceDto toPriceDto(StorePrice storePrice) {
        return PriceDto.builder()
                .id(storePrice.getId())
                .productId(storePrice.getProduct().getId())
                .productSku(storePrice.getProduct().getSku())
                .storeId(storePrice.getStoreId())
                .basePrice(storePrice.getBasePrice())
                .discountedPrice(storePrice.getDiscountedPrice())
                .finalPrice(storePrice.getCurrentPrice())
                .effectiveFrom(storePrice.getEffectiveFrom())
                .effectiveTo(storePrice.getEffectiveTo())
                .updatedAt(storePrice.getUpdatedAt())
                .build();
    }
}