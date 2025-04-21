package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.OrderContainsNoItemsException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.payment.PaymentService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.shipping.ShippingService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.purchase.PerformPurchaseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformPurchaseUseCaseImpl implements PerformPurchaseUseCase {

  private final SecurityService securityService;

  private final AvailableBookCopiesService availableBookCopiesService;

  private final CurrentOrderService currentOrderService;

  private final PaymentService paymentService;

  private final ShippingService shippingService;

  private final OrderRepository orderRepository;

  private final FinalPriceService finalPriceService;

  @Override
  @Transactional
  public Order purchase(final Order orderRequest) {
    final String username = this.securityService.getCurrentUserName();
    final Order existingOrder = this.currentOrderService.getOrCreateOrder(username);
    this.assureOrderContainsItemsForPurchase(existingOrder);
    this.assureAvailableBookCopies(existingOrder);

    log.info("Performing purchase for orderId {} for user: {}", existingOrder.getId(), username);

    this.finalPriceService.calculate(existingOrder);

    this.paymentService.processPayment(
        existingOrder.getTotalPrice(), orderRequest.getPaymentMethod(), existingOrder.getId());

    this.shippingService.processShipping(orderRequest.getShippingAddress(), existingOrder.getId());

    // Modify DB

    // Update inventory

    // Calculate loyalty points

    return null;
  }

  private void assureOrderContainsItemsForPurchase(final Order existingOrder) {
    if (existingOrder.getLines().isEmpty()) {
      throw new OrderContainsNoItemsException(existingOrder.getId());
    }
  }

  private void assureAvailableBookCopies(final Order existingOrder) {
    existingOrder.getLines().stream()
        .map(OrderItem::getAllocation)
        .forEach(
            allocation ->
                this.availableBookCopiesService.assure(
                    allocation.getBook().getId(), allocation.getCopies()));
  }
}
