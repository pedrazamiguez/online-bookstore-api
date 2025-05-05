package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.purchase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.application.coordinator.PurchaseChainCoordinator;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
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
            .supply(field(Order::getPaymentMethod), gen -> paymentMethod)
            .supply(field(Order::getShippingAddress), gen -> shippingAddress)
            .create();
    final PurchaseContext purchaseContext =
        Instancio.of(PurchaseContext.class)
            .supply(field(PurchaseContext::getUsername), gen -> username)
            .supply(field(PurchaseContext::getPaymentMethod), gen -> paymentMethod)
            .supply(field(PurchaseContext::getShippingAddress), gen -> shippingAddress)
            .supply(field(PurchaseContext::getPurchasedOrder), gen -> purchasedOrder)
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
