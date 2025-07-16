package es.pedrazamiguez.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import java.util.Optional;

@FunctionalInterface
public interface RemoveFromOrderUseCase {

  Optional<Order> removeFromOrder(Long bookId, Long copies);
}
