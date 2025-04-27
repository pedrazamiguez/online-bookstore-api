package es.pedrazamiguez.assessment.onlinebookstore.config;

import es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase.*;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PurchaseChainOrderConfiguration {

  @Bean(name = "orderedPurchaseProcessors")
  public List<PurchaseProcessor> purchaseProcessors(
      final OrderRetrievalProcessor orderRetrievalProcessor,
      final OrderValidationProcessor orderValidationProcessor,
      final StockVerificationProcessor stockVerificationProcessor,
      final PriceCalculationProcessor priceCalculationProcessor,
      final PaymentProcessor paymentProcessor,
      final ShippingProcessor shippingProcessor,
      final OrderPlacementProcessor orderPlacementProcessor,
      final InventoryUpdateProcessor inventoryUpdateProcessor,
      final LoyaltyPointsCalculationProcessor loyaltyPointsCalculationProcessor,
      final CustomerEngagementProcessor customerEngagementProcessor) {

    return List.of(
        orderRetrievalProcessor,
        orderValidationProcessor,
        stockVerificationProcessor,
        priceCalculationProcessor,
        paymentProcessor,
        shippingProcessor,
        orderPlacementProcessor,
        inventoryUpdateProcessor,
        loyaltyPointsCalculationProcessor,
        customerEngagementProcessor);
  }
}
