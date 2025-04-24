package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPlacementProcessor implements PurchaseProcessor {

  private final OrderRepository orderRepository;

  @Override
  public void process(final PurchaseContext context) {
    final Order existingOrder = context.getExistingOrder();
    log.info("Placing order for orderId {}", existingOrder.getId());

    final Order purchasedOrder =
        this.orderRepository.purchaseOrder(
            existingOrder, context.getPaymentMethod(), context.getShippingAddress());

    context.setPurchasedOrder(purchasedOrder);
    context.setTotalAmount(purchasedOrder.getTotalPrice());

    log.info("Order placed for orderId {}", purchasedOrder.getId());
    context.setOrderPlaced(true);
  }
}
