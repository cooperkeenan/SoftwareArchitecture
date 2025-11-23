package napier.destore.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import napier.destore.inventory.domain.StockItem;
import napier.destore.inventory.domain.StockItem.StockStatus;

@Repository
public interface StockRepository extends JpaRepository<StockItem, Long> {

    Optional<StockItem> findByProductIdAndStoreId(Long productId, Long storeId);

    List<StockItem> findByStoreId(Long storeId);

    List<StockItem> findByProductId(Long productId);

    List<StockItem> findByStatus(StockStatus status);

    List<StockItem> findByStoreIdAndStatus(Long storeId, StockStatus status);

    @Query("SELECT s FROM StockItem s WHERE s.storeId = :storeId AND s.quantity < s.lowStockThreshold")
    List<StockItem> findLowStockItems(@Param("storeId") Long storeId);

    @Query("SELECT s FROM StockItem s WHERE s.quantity <= 0")
    List<StockItem> findOutOfStockItems();
}