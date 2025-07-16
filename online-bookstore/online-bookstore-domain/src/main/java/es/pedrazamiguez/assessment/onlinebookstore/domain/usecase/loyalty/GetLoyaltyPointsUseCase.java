package es.pedrazamiguez.api.onlinebookstore.domain.usecase.loyalty;

@FunctionalInterface
public interface GetLoyaltyPointsUseCase {

  Long getCurrentCustomerLoyaltyPoints();
}
