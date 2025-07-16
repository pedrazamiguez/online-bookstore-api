package es.pedrazamiguez.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.onlinebookstore.domain.service.order.CurrentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRetrievalProcessor implements PurchaseProcessor {

  private final CurrentOrderService currentOrderService;

  @Override
  public void process(final PurchaseContext context) {
    final String username = context.getUsername();
    log.info("Getting current order for user: {}", username);

    final var existingOrder = this.currentOrderService.getOrCreateOrder(username);
    context.setExistingOrder(existingOrder);
  }
}
