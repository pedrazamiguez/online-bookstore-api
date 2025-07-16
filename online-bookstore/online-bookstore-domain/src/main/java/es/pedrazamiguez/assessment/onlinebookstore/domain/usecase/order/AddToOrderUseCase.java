package es.pedrazamiguez.api.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;

@FunctionalInterface
public interface AddToOrderUseCase {

  Order addToOrder(Long bookId, Long copies);
}
