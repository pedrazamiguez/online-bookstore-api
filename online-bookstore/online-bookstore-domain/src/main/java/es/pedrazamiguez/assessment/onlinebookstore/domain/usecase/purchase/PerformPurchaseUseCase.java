package es.pedrazamiguez.api.onlinebookstore.domain.usecase.purchase;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;

@FunctionalInterface
public interface PerformPurchaseUseCase {

  Order purchase(Order orderRequest);
}
