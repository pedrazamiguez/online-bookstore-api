package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.customer.LoyaltyPointsService;
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
        final var existingOrder = context.getExistingOrder();

        log.info("Calculating loyalty points for order: {}", existingOrder.getId());
        final var loyaltyPoints = this.loyaltyPointsService.calculateLoyaltyPoints(existingOrder);

        log.info("Loyalty points calculated: {}", loyaltyPoints);
        context.setLoyaltyPointsEarned(loyaltyPoints);
    }
}
