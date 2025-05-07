package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.CurrentOrderService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderRetrievalProcessorTest {

  @InjectMocks private OrderRetrievalProcessor orderRetrievalProcessor;

  @Mock private CurrentOrderService currentOrderService;

  @Test
  void givenCurrentUser_whenProcess_thenRetrieveOrder() {
    // GIVEN
    final var currentUser = Instancio.create(String.class);
    final var context =
        Instancio.of(PurchaseContext.class)
            .supply(field(PurchaseContext::getUsername), gen -> currentUser)
            .supply(field(PurchaseContext::getExistingOrder), gen -> null)
            .create();

    final Order existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();

    doAnswer(invocation -> existingOrder)
        .when(this.currentOrderService)
        .getOrCreateOrder(currentUser);

    // WHEN
    assertThatCode(() -> this.orderRetrievalProcessor.process(context)).doesNotThrowAnyException();

    // THEN
    assertThat(context.getExistingOrder()).isEqualTo(existingOrder);
    verify(this.currentOrderService).getOrCreateOrder(currentUser);
    verifyNoMoreInteractions(this.currentOrderService);
  }
}
