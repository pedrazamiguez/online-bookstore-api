package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoyaltyPointsCalculationProcessor implements PurchaseProcessor {

  @Override
  public void process(final PurchaseContext context) {
    final var username = context.getUsername();
    final var existingOrder = context.getExistingOrder();

    log.info(
        "Calculating loyalty points for orderId {} and user {}", existingOrder.getId(), username);
  }
}
