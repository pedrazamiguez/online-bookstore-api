package es.pedrazamiguez.onlinebookstore.domain.usecase.purchase;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;

@FunctionalInterface
public interface PerformPurchaseUseCase {

  Order purchase(Order orderRequest);
}
