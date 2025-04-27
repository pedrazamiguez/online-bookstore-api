package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.loyalty;

@FunctionalInterface
public interface GetLoyaltyPointsUseCase {

  Long getCurrentCustomerLoyaltyPoints();
}
