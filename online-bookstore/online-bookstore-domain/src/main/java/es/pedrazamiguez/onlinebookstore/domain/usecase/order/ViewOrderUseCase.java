package es.pedrazamiguez.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import java.util.Optional;

@FunctionalInterface
public interface ViewOrderUseCase {

  Optional<Order> getCurrentOrderForCustomer();
}
