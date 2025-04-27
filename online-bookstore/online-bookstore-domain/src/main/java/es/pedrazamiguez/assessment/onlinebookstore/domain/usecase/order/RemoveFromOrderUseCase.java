package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import java.util.Optional;

@FunctionalInterface
public interface RemoveFromOrderUseCase {

    Optional<Order> removeFromOrder(Long bookId, Long copies);
}
