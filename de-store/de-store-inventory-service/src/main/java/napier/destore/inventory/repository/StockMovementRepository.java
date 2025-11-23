package napier.destore.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import napier.destore.inventory.domain.StockMovement;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByStockItemId(Long stockItemId);

    List<StockMovement> findByStockItemIdAndCreatedAtAfter(Long stockItemId, LocalDateTime after);
}