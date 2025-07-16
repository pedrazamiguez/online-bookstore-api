package es.pedrazamiguez.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.onlinebookstore.domain.service.customer.LoyaltyPointsService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoyaltyPointsCalculationProcessorTest {

  @InjectMocks private LoyaltyPointsCalculationProcessor loyaltyPointsCalculationProcessor;

  @Mock private LoyaltyPointsService loyaltyPointsService;

  @Test
  void givenOrder_whenProcess_thenLoyaltyPointsCalculated() {
    // GIVEN
    final var purchasedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getLines), gen -> gen.collection().size(3))
            .create();
    final var context =
        Instancio.of(PurchaseContext.class)
            .set(field(PurchaseContext::getPurchasedOrder), purchasedOrder)
            .create();

    final var loyaltyPoints = 6L;
    when(this.loyaltyPointsService.calculateLoyaltyPoints(purchasedOrder))
        .thenReturn(loyaltyPoints);

    // WHEN
    assertThatCode(() -> this.loyaltyPointsCalculationProcessor.process(context))
        .doesNotThrowAnyException();

    // THEN
    assertThat(context.getPurchasedOrder()).isEqualTo(purchasedOrder);
    assertThat(context.getLoyaltyPointsEarned()).isEqualTo(loyaltyPoints);
    verify(this.loyaltyPointsService).calculateLoyaltyPoints(purchasedOrder);
    verifyNoMoreInteractions(this.loyaltyPointsService);
  }
}
