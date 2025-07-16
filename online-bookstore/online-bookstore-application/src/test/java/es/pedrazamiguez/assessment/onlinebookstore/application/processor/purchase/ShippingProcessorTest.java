package es.pedrazamiguez.api.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.api.onlinebookstore.domain.service.shipping.ShippingService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShippingProcessorTest {

  @InjectMocks private ShippingProcessor shippingProcessor;

  @Mock private ShippingService shippingService;

  @Test
  void givenOrder_whenProcess_thenPrepareShipping() {
    // GIVEN
    final var existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();
    final var context =
        Instancio.of(PurchaseContext.class)
            .set(field(PurchaseContext::getExistingOrder), existingOrder)
            .create();

    // WHEN
    assertThatCode(() -> this.shippingProcessor.process(context)).doesNotThrowAnyException();

    // THEN
    assertThat(context.isShippingPrepared()).isTrue();
    verify(this.shippingService)
        .processShipping(context.getShippingAddress(), existingOrder.getId());
    verifyNoMoreInteractions(this.shippingService);
  }
}
