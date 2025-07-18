package es.pedrazamiguez.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.onlinebookstore.domain.exception.OrderContainsNoItemsException;
import es.pedrazamiguez.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.onlinebookstore.domain.processor.PurchaseProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderValidationProcessor implements PurchaseProcessor {

  @Override
  public void process(final PurchaseContext context) {
    final var existingOrder = context.getExistingOrder();
    log.info("Validating order contents for orderId {}", existingOrder.getId());
    if (existingOrder.getLines().isEmpty()) {
      throw new OrderContainsNoItemsException(existingOrder.getId());
    }
  }
}
