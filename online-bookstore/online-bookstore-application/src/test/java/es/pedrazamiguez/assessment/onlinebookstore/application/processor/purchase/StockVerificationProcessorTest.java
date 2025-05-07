package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockVerificationProcessorTest {

  @InjectMocks private StockVerificationProcessor stockVerificationProcessor;

  @Mock private AvailableBookCopiesService availableBookCopiesService;

  @Test
  void givenOrderWithLines_whenProcess_thenAssureStockAvailabilityPerLine() {
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
    assertThatCode(() -> this.stockVerificationProcessor.process(context))
        .doesNotThrowAnyException();

    // THEN
    for (final var orderItem : existingOrder.getLines()) {
      final var allocation = orderItem.getAllocation();
      verify(this.availableBookCopiesService)
          .assure(allocation.getBook().getId(), allocation.getCopies());
    }
    verifyNoMoreInteractions(this.availableBookCopiesService);
  }
}
