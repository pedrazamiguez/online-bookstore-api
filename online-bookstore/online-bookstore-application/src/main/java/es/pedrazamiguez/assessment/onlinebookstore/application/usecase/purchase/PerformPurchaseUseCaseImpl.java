package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.application.coordinator.PurchaseChainCoordinator;
import es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase.*;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.LoyaltyPointRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.customer.LoyaltyPointsService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.payment.PaymentService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.shipping.ShippingService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.purchase.PerformPurchaseUseCase;
import java.util.ArrayList;
import java.util.List;
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

  private final FinalPriceService finalPriceService;

  private final LoyaltyPointsService loyaltyPointsService;

  private final OrderRepository orderRepository;

  private final BookCopyRepository bookCopyRepository;

  private final LoyaltyPointRepository loyaltyPointRepository;

  @Override
  @Transactional
  public Order purchase(final Order orderRequest) {
    final String username = this.securityService.getCurrentUserName();
    final PaymentMethod paymentMethod = orderRequest.getPaymentMethod();
    final String shippingAddress = orderRequest.getShippingAddress();

    // Configure processors
    final List<PurchaseProcessor> processors = new ArrayList<>();
    processors.add(new OrderRetrievalProcessor(this.currentOrderService));
    processors.add(new OrderValidationProcessor());
    processors.add(new StockVerificationProcessor(this.availableBookCopiesService));
    processors.add(new PriceCalculationProcessor(this.finalPriceService));
    processors.add(new PaymentProcessor(this.paymentService));
    processors.add(new ShippingProcessor(this.shippingService));
    processors.add(new OrderPlacementProcessor(this.orderRepository));
    processors.add(new InventoryUpdateProcessor(this.bookCopyRepository));
    processors.add(new LoyaltyPointsCalculationProcessor(this.loyaltyPointsService));
    processors.add(new CustomerEngagementProcessor(this.loyaltyPointRepository));

    // Coordinate
    final PurchaseChainCoordinator purchaseChainCoordinator =
        new PurchaseChainCoordinator(processors);

    // Execute the chain
    final PurchaseContext purchaseContext =
        purchaseChainCoordinator.executeChain(username, paymentMethod, shippingAddress);
    return purchaseContext.getPurchasedOrder();
  }
}
