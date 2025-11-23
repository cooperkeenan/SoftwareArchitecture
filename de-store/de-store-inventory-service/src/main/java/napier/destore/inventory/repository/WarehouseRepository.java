package napier.destore.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import napier.destore.inventory.domain.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByName(String name);

    List<Warehouse> findByRegion(String region);

    Optional<Warehouse> findByIsCentralTrue();
}