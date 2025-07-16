package es.pedrazamiguez.api.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.api.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.api.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.api.onlinebookstore.domain.service.shipping.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShippingProcessor implements PurchaseProcessor {

  private final ShippingService shippingService;

  @Override
  public void process(final PurchaseContext context) {
    final var existingOrder = context.getExistingOrder();

    log.info("Preparing shipping for orderId {}", existingOrder.getId());
    this.shippingService.processShipping(context.getShippingAddress(), existingOrder.getId());

    log.info("Shipping prepared for orderId {}", existingOrder.getId());
    context.setShippingPrepared(true);
  }
}
