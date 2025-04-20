package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.CurrentOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
