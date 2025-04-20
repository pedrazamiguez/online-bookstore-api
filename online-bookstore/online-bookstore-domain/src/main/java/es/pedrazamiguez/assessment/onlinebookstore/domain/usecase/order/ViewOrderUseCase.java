package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import java.util.Optional;

public interface ViewOrderUseCase {

  Optional<Order> getCurrentOrderForCustomer();
}
