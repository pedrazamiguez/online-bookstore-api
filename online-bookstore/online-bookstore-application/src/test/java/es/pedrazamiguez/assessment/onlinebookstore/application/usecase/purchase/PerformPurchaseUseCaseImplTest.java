package es.pedrazamiguez.api.onlinebookstore.application.usecase.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.application.coordinator.PurchaseChainCoordinator;
import es.pedrazamiguez.api.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.api.onlinebookstore.domain.service.security.SecurityService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PerformPurchaseUseCaseImplTest {

  @InjectMocks private PerformPurchaseUseCaseImpl performPurchaseUseCase;

  @Mock private SecurityService securityService;

  @Mock private PurchaseChainCoordinator purchaseChainCoordinator;

  @Test
  void givenPaymentMethodAndShippingAddress_whenPurchase_thenReturnPurchasedOrder() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final PaymentMethod paymentMethod = Instancio.create(PaymentMethod.class);
    final String shippingAddress = Instancio.create(String.class);

    final Order purchasedOrder =
        Instancio.of(Order.class)
            .set(field(Order::getPaymentMethod), paymentMethod)
            .set(field(Order::getShippingAddress), shippingAddress)
            .create();
    final PurchaseContext purchaseContext =
        Instancio.of(PurchaseContext.class)
            .set(field(PurchaseContext::getUsername), username)
            .set(field(PurchaseContext::getPaymentMethod), paymentMethod)
            .set(field(PurchaseContext::getShippingAddress), shippingAddress)
            .set(field(PurchaseContext::getPurchasedOrder), purchasedOrder)
            .create();

    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.purchaseChainCoordinator.executeChain(username, paymentMethod, shippingAddress))
        .thenReturn(purchaseContext);
    when(this.purchaseChainCoordinator.getPurchasedOrder(purchaseContext))
        .thenReturn(purchasedOrder);

    // WHEN
    final Order result = this.performPurchaseUseCase.purchase(purchasedOrder);

    // THEN
    assertThat(result.getPaymentMethod()).isEqualTo(paymentMethod);
    assertThat(result.getShippingAddress()).isEqualTo(shippingAddress);

    verify(this.securityService).getCurrentUserName();
    verify(this.purchaseChainCoordinator).executeChain(username, paymentMethod, shippingAddress);
    verify(this.purchaseChainCoordinator).getPurchasedOrder(purchaseContext);
    verifyNoMoreInteractions(this.securityService, this.purchaseChainCoordinator);
  }
}
