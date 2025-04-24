package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.FinalPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCalculationProcessor implements PurchaseProcessor {

  private final FinalPriceService finalPriceService;

  @Override
  public void process(final PurchaseContext context) {
    final var existingOrder = context.getExistingOrder();
    log.info("Calculating subtotal for orderId {}", existingOrder.getId());
    this.finalPriceService.calculate(existingOrder);
  }
}
