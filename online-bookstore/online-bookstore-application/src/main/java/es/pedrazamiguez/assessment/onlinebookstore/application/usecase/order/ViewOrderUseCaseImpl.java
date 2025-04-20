package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.SecurityService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewOrderUseCaseImpl implements ViewOrderUseCase {

  private final SecurityService securityService;

  @Override
  public Optional<Order> getCurrentOrderForCustomer() {
    final String username = this.securityService.getCurrentUserName();
    return Optional.empty();
  }
}
