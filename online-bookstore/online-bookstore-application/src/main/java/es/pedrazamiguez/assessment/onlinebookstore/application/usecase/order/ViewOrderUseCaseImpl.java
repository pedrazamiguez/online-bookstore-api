package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.FullPriceService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class ViewOrderUseCaseImpl implements ViewOrderUseCase {

  private final SecurityService securityService;

  private final CurrentOrderService currentOrderService;

  private final FullPriceService fullPriceService;

  @Override
  @Transactional
  public Optional<Order> getCurrentOrderForCustomer() {
    final String username = this.securityService.getCurrentUserName();
    final Order existingOrder = this.currentOrderService.getOrCreateOrder(username);

    if (CollectionUtils.isEmpty(existingOrder.getLines())) {
      return Optional.empty();
    }

    this.fullPriceService.calculate(existingOrder);
    return Optional.of(existingOrder);
  }
}
