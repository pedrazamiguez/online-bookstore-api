package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderPlacementProcessorTest {

  @InjectMocks private OrderPlacementProcessor orderPlacementProcessor;

  @Mock private OrderRepository orderRepository;

  @Test
  void givenExistingOrder_whenProcess_thenOrderIsPlaced() {
    // GIVEN
    final var existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();
    final var context =
        Instancio.of(PurchaseContext.class)
            .supply(field(PurchaseContext::getExistingOrder), gen -> existingOrder)
            .create();

    final var purchasedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();

    when(this.orderRepository.purchaseOrder(
            existingOrder, context.getPaymentMethod(), context.getShippingAddress()))
        .thenReturn(purchasedOrder);

    // WHEN
    assertThatCode(() -> this.orderPlacementProcessor.process(context)).doesNotThrowAnyException();

    // THEN
    assertThat(context.getPurchasedOrder()).isEqualTo(purchasedOrder);
    assertThat(context.getTotalAmount()).isEqualTo(purchasedOrder.getTotalPrice());
    assertThat(context.isOrderPlaced()).isTrue();
    verify(this.orderRepository)
        .purchaseOrder(existingOrder, context.getPaymentMethod(), context.getShippingAddress());
    verifyNoMoreInteractions(this.orderRepository);
  }
}
