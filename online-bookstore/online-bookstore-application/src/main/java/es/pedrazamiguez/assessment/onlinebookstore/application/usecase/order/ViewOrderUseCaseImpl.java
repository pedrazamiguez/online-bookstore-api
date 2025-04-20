package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewOrderUseCaseImpl implements ViewOrderUseCase {

  private final SecurityService securityService;

  private final OrderRepository orderRepository;

  @Override
  public Optional<Order> getCurrentOrderForCustomer() {
    final String username = this.securityService.getCurrentUserName();
    return this.orderRepository.findCreatedOrderForCustomer(username);
  }
}
