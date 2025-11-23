package napier.destore.price.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import napier.destore.price.domain.StorePrice;

@Repository
public interface StorePriceRepository extends JpaRepository<StorePrice, Long> {

    Optional<StorePrice> findByProductIdAndStoreId(Long productId, Long storeId);

    List<StorePrice> findByStoreId(Long storeId);

    List<StorePrice> findByProductId(Long productId);

    @Query("SELECT sp FROM StorePrice sp WHERE sp.storeId = :storeId AND sp.activePromotion IS NOT NULL")
    List<StorePrice> findByStoreIdWithActivePromotion(@Param("storeId") Long storeId);

    void deleteByProductId(Long productId);
}