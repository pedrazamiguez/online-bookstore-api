package es.pedrazamiguez.api.onlinebookstore.application.processor.purchase;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import es.pedrazamiguez.api.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.LoyaltyPointRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerEngagementProcessorTest {

  @InjectMocks private CustomerEngagementProcessor customerEngagementProcessor;

  @Mock private LoyaltyPointRepository loyaltyPointRepository;

  @Test
  void givenOrder_whenProcess_thenEngageCustomer() {
    // GIVEN
    final var context = Instancio.create(PurchaseContext.class);

    // WHEN
    assertThatCode(() -> this.customerEngagementProcessor.process(context))
        .doesNotThrowAnyException();

    // THEN
    verify(this.loyaltyPointRepository)
        .addLoyaltyPoints(
            context.getUsername(),
            context.getPurchasedOrder().getId(),
            context.getLoyaltyPointsEarned());
    verifyNoMoreInteractions(this.loyaltyPointRepository);
  }
}
