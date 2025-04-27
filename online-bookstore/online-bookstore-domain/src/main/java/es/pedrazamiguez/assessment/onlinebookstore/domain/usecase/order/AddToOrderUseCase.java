package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;

@FunctionalInterface
public interface AddToOrderUseCase {

    Order addToOrder(Long bookId, Long copies);
}
