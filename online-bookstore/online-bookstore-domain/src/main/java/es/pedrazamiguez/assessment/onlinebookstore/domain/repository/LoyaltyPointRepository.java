package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

public interface LoyaltyPointRepository {

    void addLoyaltyPoints(String username, Long orderId, Long points);

    void redeemLoyaltyPoints(String username, Long orderId, Long points);

    Long getLoyaltyPoints(String username);
}
