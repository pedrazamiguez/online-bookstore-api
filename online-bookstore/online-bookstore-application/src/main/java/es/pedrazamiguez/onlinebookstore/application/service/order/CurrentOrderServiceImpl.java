package es.pedrazamiguez.onlinebookstore.application.service.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.onlinebookstore.domain.service.order.CurrentOrderService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentOrderServiceImpl implements CurrentOrderService {

  private final OrderRepository orderRepository;

  @Override
  public Order getOrCreateOrder(final String username) {
    final Optional<Order> optionalOrder =
        this.orderRepository.findCreatedOrderForCustomer(username);

    return optionalOrder.orElseGet(() -> this.orderRepository.createNewOrder(username));
  }
}
