package es.pedrazamiguez.onlinebookstore.domain.usecase.loyalty;

@FunctionalInterface
public interface GetLoyaltyPointsUseCase {

  Long getCurrentCustomerLoyaltyPoints();
}
