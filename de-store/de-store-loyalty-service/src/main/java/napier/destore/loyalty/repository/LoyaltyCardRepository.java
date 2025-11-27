package napier.destore.loyalty.repository;

import napier.destore.loyalty.domain.LoyaltyCard;
import napier.destore.loyalty.domain.LoyaltyCard.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyCardRepository extends JpaRepository<LoyaltyCard, Long> {

    Optional<LoyaltyCard> findByCardNumber(String cardNumber);

    Optional<LoyaltyCard> findByCustomerEmail(String customerEmail);

    List<LoyaltyCard> findByTier(LoyaltyTier tier);

    List<LoyaltyCard> findByActiveTrue();

    boolean existsByCustomerEmail(String customerEmail);
}