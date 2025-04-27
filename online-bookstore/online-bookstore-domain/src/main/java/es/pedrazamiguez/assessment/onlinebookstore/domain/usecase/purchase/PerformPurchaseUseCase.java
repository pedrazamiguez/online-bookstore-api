package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;

@FunctionalInterface
public interface PerformPurchaseUseCase {

  Order purchase(Order orderRequest);
}
