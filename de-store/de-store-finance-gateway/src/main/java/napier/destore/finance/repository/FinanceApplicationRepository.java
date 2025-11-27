package napier.destore.finance.repository;

import napier.destore.finance.domain.ApplicationStatus;
import napier.destore.finance.domain.FinanceApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinanceApplicationRepository extends JpaRepository<FinanceApplication, Long> {

    Optional<FinanceApplication> findByApplicationReference(String applicationReference);

    List<FinanceApplication> findByStoreId(Long storeId);

    List<FinanceApplication> findByStatus(ApplicationStatus status);

    List<FinanceApplication> findByStoreIdAndStatus(Long storeId, ApplicationStatus status);
}