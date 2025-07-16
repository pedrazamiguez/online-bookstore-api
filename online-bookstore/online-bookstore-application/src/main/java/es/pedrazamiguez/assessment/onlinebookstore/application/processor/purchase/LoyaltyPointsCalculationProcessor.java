package es.pedrazamiguez.api.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.api.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.api.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.api.onlinebookstore.domain.service.customer.LoyaltyPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoyaltyPointsCalculationProcessor implements PurchaseProcessor {

  private final LoyaltyPointsService loyaltyPointsService;

  @Override
  public void process(final PurchaseContext context) {
    final var purchasedOrder = context.getPurchasedOrder();

    log.info("Calculating loyalty points for order: {}", purchasedOrder.getId());
    final var loyaltyPoints = this.loyaltyPointsService.calculateLoyaltyPoints(purchasedOrder);

    log.info("Loyalty points calculated: {}", loyaltyPoints);
    context.setLoyaltyPointsEarned(loyaltyPoints);
  }
}
