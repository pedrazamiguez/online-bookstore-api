package es.pedrazamiguez.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;

@FunctionalInterface
public interface AddToOrderUseCase {

  Order addToOrder(Long bookId, Long copies);
}
