package es.pedrazamiguez.api.onlinebookstore.application.usecase.order;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.api.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
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

  private final FinalPriceService finalPriceService;

  @Override
  @Transactional
  public Optional<Order> getCurrentOrderForCustomer() {
    final String username = this.securityService.getCurrentUserName();
    final Order existingOrder = this.currentOrderService.getOrCreateOrder(username);

    if (CollectionUtils.isEmpty(existingOrder.getLines())) {
      return Optional.empty();
    }

    this.finalPriceService.calculate(existingOrder);
    return Optional.of(existingOrder);
  }
}
