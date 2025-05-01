package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.instancio.Select.field;

import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.OrderContainsNoItemsException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import java.util.Collections;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderValidationProcessorTest {

  @InjectMocks private OrderValidationProcessor orderValidationProcessor;

  @Test
  void givenOrderWithNoItems_whenProcess_thenThrowException() {
    // GIVEN
    final var existingOrder =
        Instancio.of(Order.class)
            .supply(field(Order::getLines), gen -> Collections.emptyList())
            .create();
    final var context =
        Instancio.of(PurchaseContext.class)
            .supply(field(PurchaseContext::getExistingOrder), gen -> existingOrder)
            .create();

    // WHEN
    assertThatCode(() -> this.orderValidationProcessor.process(context))
        .isInstanceOf(OrderContainsNoItemsException.class)
        .hasMessageContaining(
            "Order with id %s contains no items for purchase", existingOrder.getId());
  }

  @Test
  void givenOrderWithItems_whenProcess_thenDoNotThrowException() {
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
    assertThatCode(() -> this.orderValidationProcessor.process(context)).doesNotThrowAnyException();
  }
}
