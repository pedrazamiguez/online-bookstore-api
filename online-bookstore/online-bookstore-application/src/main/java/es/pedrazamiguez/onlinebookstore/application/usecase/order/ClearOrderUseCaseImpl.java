package es.pedrazamiguez.onlinebookstore.application.usecase.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.onlinebookstore.domain.usecase.order.ClearOrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearOrderUseCaseImpl implements ClearOrderUseCase {

  private final SecurityService securityService;

  private final CurrentOrderService currentOrderService;

  private final OrderRepository orderRepository;

  @Override
  @Transactional
  public void clearOrderItems() {
    final String username = this.securityService.getCurrentUserName();

    log.info("Clearing order items for user: {}", username);
    final Order existingOrder = this.currentOrderService.getOrCreateOrder(username);

    this.orderRepository.deleteOrderItems(existingOrder.getId());
  }
}
