package napier.destore.loyalty.repository;

import napier.destore.loyalty.domain.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Long> {

    List<PointsTransaction> findByLoyaltyCardId(Long loyaltyCardId);

    List<PointsTransaction> findByLoyaltyCardIdAndCreatedAtAfter(Long loyaltyCardId, LocalDateTime after);

    List<PointsTransaction> findByStoreId(Long storeId);
}