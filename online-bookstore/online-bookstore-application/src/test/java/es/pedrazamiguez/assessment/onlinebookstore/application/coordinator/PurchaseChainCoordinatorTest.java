package es.pedrazamiguez.assessment.onlinebookstore.application.coordinator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PurchaseStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.PurchaseException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseChainCoordinatorTest {

  @InjectMocks private PurchaseChainCoordinator purchaseChainCoordinator;

  @Mock private PurchaseProcessor processor1;
  @Mock private PurchaseProcessor processor2;

  @BeforeEach
  void setUp() {
    final List<PurchaseProcessor> processors = List.of(this.processor1, this.processor2);
    this.purchaseChainCoordinator = new PurchaseChainCoordinator(processors);
  }

  @Test
  void givenValidInput_whenExecuteChain_thenAllProcessorsRunAndContextIsUpdated() {
    // GIVEN
    final String username = "testUser";
    final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    final String shippingAddress = "123 Main St";
    final var purchasedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();

    doAnswer(
            invocation -> {
              final PurchaseContext context = invocation.getArgument(0);
              context.setPurchasedOrder(purchasedOrder);
              context.setStatus(PurchaseStatus.SUCCESS);
              return null;
            })
        .when(this.processor1)
        .process(any(PurchaseContext.class));
    doNothing().when(this.processor2).process(any(PurchaseContext.class));

    // WHEN
    final PurchaseContext result =
        this.purchaseChainCoordinator.executeChain(username, paymentMethod, shippingAddress);

    // THEN
    assertThat(result.getUsername()).isEqualTo(username);
    assertThat(result.getPaymentMethod()).isEqualTo(paymentMethod);
    assertThat(result.getShippingAddress()).isEqualTo(shippingAddress);
    assertThat(result.getPurchasedOrder()).isEqualTo(purchasedOrder);
    assertThat(result.getStatus()).isEqualTo(PurchaseStatus.SUCCESS);
    assertThat(result.isSuccessful()).isTrue();
    assertThat(this.purchaseChainCoordinator.getPurchasedOrder(result)).isEqualTo(purchasedOrder);
    verify(this.processor1).process(any(PurchaseContext.class));
    verify(this.processor2).process(any(PurchaseContext.class));
    verifyNoMoreInteractions(this.processor1, this.processor2);
  }

  @Test
  void
      givenProcessorThrowsException_whenExecuteChain_thenThrowsPurchaseExceptionAndSetsFailedStatus() {
    // GIVEN
    final String username = "testUser";
    final PaymentMethod paymentMethod = PaymentMethod.PAYPAL;
    final String shippingAddress = "456 Elm St";
    final String errorMessage = "Order validation failed";
    final RuntimeException exception = new RuntimeException(errorMessage);

    doThrow(exception).when(this.processor1).process(any(PurchaseContext.class));

    // WHEN
    assertThatThrownBy(
            () ->
                this.purchaseChainCoordinator.executeChain(
                    username, paymentMethod, shippingAddress))
        .isInstanceOf(PurchaseException.class)
        .hasMessageContaining("Purchase failed: " + errorMessage)
        .hasCause(exception)
        .asInstanceOf(type(PurchaseException.class))
        .satisfies(
            purchaseException -> {
              final PurchaseContext context = purchaseException.getContext();
              assertThat(context.getUsername()).isEqualTo(username);
              assertThat(context.getPaymentMethod()).isEqualTo(paymentMethod);
              assertThat(context.getShippingAddress()).isEqualTo(shippingAddress);
              assertThat(context.getStatus()).isEqualTo(PurchaseStatus.FAILED);
              assertThat(context.getErrorMessage()).isEqualTo(errorMessage);
              assertThat(context.isSuccessful()).isFalse();
            });

    // THEN
    verify(this.processor1).process(any(PurchaseContext.class));
    verifyNoInteractions(this.processor2);
    verifyNoMoreInteractions(this.processor1);
  }

  @Test
  void givenUnsuccessfulContext_whenExecuteChain_thenStopsProcessing() {
    // GIVEN
    final String username = "testUser";
    final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    final String shippingAddress = "789 Oak St";

    doAnswer(
            invocation -> {
              final PurchaseContext context = invocation.getArgument(0);
              context.setStatus(PurchaseStatus.FAILED);
              return null;
            })
        .when(this.processor1)
        .process(any(PurchaseContext.class));

    // WHEN
    final PurchaseContext result =
        this.purchaseChainCoordinator.executeChain(username, paymentMethod, shippingAddress);

    // THEN
    assertThat(result.getUsername()).isEqualTo(username);
    assertThat(result.getPaymentMethod()).isEqualTo(paymentMethod);
    assertThat(result.getShippingAddress()).isEqualTo(shippingAddress);
    assertThat(result.getStatus()).isEqualTo(PurchaseStatus.FAILED);
    assertThat(result.isSuccessful()).isFalse();
    verify(this.processor1).process(any(PurchaseContext.class));
    verifyNoInteractions(this.processor2); // processor2 should not be called
    verifyNoMoreInteractions(this.processor1);
  }

  @Test
  void givenPurchaseContext_whenGetPurchasedOrder_thenReturnsPurchasedOrder() {
    // GIVEN
    final var purchasedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();
    final var context =
        Instancio.of(PurchaseContext.class)
            .set(field(PurchaseContext::getPurchasedOrder), purchasedOrder)
            .create();

    // WHEN
    final Order result = this.purchaseChainCoordinator.getPurchasedOrder(context);

    // THEN
    assertThat(result).isEqualTo(purchasedOrder);
  }
}
