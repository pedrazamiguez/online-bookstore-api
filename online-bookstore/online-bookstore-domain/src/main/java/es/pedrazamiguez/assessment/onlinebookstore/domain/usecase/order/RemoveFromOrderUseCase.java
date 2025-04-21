package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import java.util.Optional;

@FunctionalInterface
public interface RemoveFromOrderUseCase {

  Optional<Order> removeFromOrder(Long bookId, Long copies);
}
