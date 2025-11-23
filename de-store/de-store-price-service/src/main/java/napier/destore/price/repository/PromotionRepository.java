package napier.destore.price.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import napier.destore.price.domain.Promotion;
import napier.destore.price.domain.PromotionType;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByStoreId(Long storeId);

    List<Promotion> findByType(PromotionType type);

    List<Promotion> findByActiveTrue();

    @Query("SELECT p FROM Promotion p WHERE p.active = true AND " +
           "(p.storeId IS NULL OR p.storeId = :storeId) AND " +
           "(p.startDate IS NULL OR p.startDate <= :now) AND " +
           "(p.endDate IS NULL OR p.endDate >= :now)")
    List<Promotion> findActivePromotionsForStore(@Param("storeId") Long storeId, @Param("now") LocalDateTime now);
}