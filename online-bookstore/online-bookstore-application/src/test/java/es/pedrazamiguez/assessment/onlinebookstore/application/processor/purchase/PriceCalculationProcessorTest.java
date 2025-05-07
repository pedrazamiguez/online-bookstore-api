package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.instancio.Select.field;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.FinalPriceService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriceCalculationProcessorTest {

  @InjectMocks private PriceCalculationProcessor priceCalculationProcessor;

  @Mock private FinalPriceService finalPriceService;

  @Test
  void givenOrder_whenProcess_thenCalculateSubtotal() {
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
    assertThatCode(() -> this.priceCalculationProcessor.process(context))
        .doesNotThrowAnyException();

    // THEN
    assertThat(existingOrder.getTotalPrice()).isNotNull();
    verify(this.finalPriceService).calculate(existingOrder);
    verifyNoMoreInteractions(this.finalPriceService);
  }
}
