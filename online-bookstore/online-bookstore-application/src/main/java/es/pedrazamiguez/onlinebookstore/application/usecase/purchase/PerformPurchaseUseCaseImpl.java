package es.pedrazamiguez.onlinebookstore.application.usecase.purchase;

import es.pedrazamiguez.onlinebookstore.application.coordinator.PurchaseChainCoordinator;
import es.pedrazamiguez.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.onlinebookstore.domain.usecase.purchase.PerformPurchaseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformPurchaseUseCaseImpl implements PerformPurchaseUseCase {

  private final SecurityService securityService;

  private final PurchaseChainCoordinator purchaseChainCoordinator;

  @Override
  @Transactional
  public Order purchase(final Order orderRequest) {
    final String username = this.securityService.getCurrentUserName();
    final PaymentMethod paymentMethod = orderRequest.getPaymentMethod();
    final String shippingAddress = orderRequest.getShippingAddress();

    final PurchaseContext purchaseContext =
        this.purchaseChainCoordinator.executeChain(username, paymentMethod, shippingAddress);

    return this.purchaseChainCoordinator.getPurchasedOrder(purchaseContext);
  }
}
