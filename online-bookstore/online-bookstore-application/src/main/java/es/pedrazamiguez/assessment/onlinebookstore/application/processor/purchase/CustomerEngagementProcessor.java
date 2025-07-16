package es.pedrazamiguez.api.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.api.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.api.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.LoyaltyPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEngagementProcessor implements PurchaseProcessor {

  private final LoyaltyPointRepository loyaltyPointRepository;

  @Override
  public void process(final PurchaseContext context) {
    log.info("Saving earned loyalty points for user: {}", context.getUsername());
    final String username = context.getUsername();
    final Long orderId = context.getPurchasedOrder().getId();
    final Long loyaltyPoints = context.getLoyaltyPointsEarned();

    this.loyaltyPointRepository.addLoyaltyPoints(username, orderId, loyaltyPoints);
  }
}
