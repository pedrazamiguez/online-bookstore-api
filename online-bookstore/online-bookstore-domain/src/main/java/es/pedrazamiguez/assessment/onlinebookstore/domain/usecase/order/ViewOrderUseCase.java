package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import java.util.Optional;

@FunctionalInterface
public interface ViewOrderUseCase {

    Optional<Order> getCurrentOrderForCustomer();
}
