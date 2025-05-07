package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.payment.PaymentService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

  @InjectMocks private PaymentProcessor paymentProcessor;

  @Mock private PaymentService paymentService;

  @Test
  void givenOrder_whenProcess_thenProcessPayment() {
    // GIVEN
    final var existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();
    final var context =
        Instancio.of(PurchaseContext.class)
            .supply(field(PurchaseContext::getExistingOrder), gen -> existingOrder)
            .create();

    // WHEN
    assertThatCode(() -> this.paymentProcessor.process(context)).doesNotThrowAnyException();

    // THEN
    assertThat(context.isPaymentProcessed()).isTrue();
    verify(this.paymentService)
        .processPayment(
            existingOrder.getTotalPrice(), context.getPaymentMethod(), existingOrder.getId());
    verifyNoMoreInteractions(this.paymentService);
  }
}
