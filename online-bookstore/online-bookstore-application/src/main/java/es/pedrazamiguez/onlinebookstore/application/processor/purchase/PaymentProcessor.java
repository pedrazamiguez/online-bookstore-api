package es.pedrazamiguez.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.onlinebookstore.domain.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProcessor implements PurchaseProcessor {

  private final PaymentService paymentService;

  @Override
  public void process(final PurchaseContext context) {
    final var existingOrder = context.getExistingOrder();

    log.info("Processing payment for orderId {}", existingOrder.getId());
    this.paymentService.processPayment(
        existingOrder.getTotalPrice(), context.getPaymentMethod(), existingOrder.getId());

    log.info("Payment processed for orderId {}", existingOrder.getId());
    context.setPaymentProcessed(true);
  }
}
