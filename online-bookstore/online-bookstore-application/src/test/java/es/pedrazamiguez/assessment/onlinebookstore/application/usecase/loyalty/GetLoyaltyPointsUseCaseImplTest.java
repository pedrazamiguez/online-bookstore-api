package es.pedrazamiguez.api.onlinebookstore.application.usecase.loyalty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.repository.LoyaltyPointRepository;
import es.pedrazamiguez.api.onlinebookstore.domain.service.security.SecurityService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetLoyaltyPointsUseCaseImplTest {

  @InjectMocks private GetLoyaltyPointsUseCaseImpl getLoyaltyPointsUseCase;

  @Mock private SecurityService securityService;

  @Mock private LoyaltyPointRepository loyaltyPointRepository;

  @Test
  void givenUsername_whenGetCurrentCustomerLoyaltyPoints_thenGetLoyaltyPoints() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Long expectedLoyaltyPoints =
        Instancio.of(Long.class).supply(all(Long.class), gen -> gen.longRange(1L, 1000L)).create();

    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.loyaltyPointRepository.getLoyaltyPoints(username)).thenReturn(expectedLoyaltyPoints);

    // WHEN
    final Long result = this.getLoyaltyPointsUseCase.getCurrentCustomerLoyaltyPoints();

    // THEN
    assertThat(result).isEqualTo(expectedLoyaltyPoints);
    verify(this.securityService).getCurrentUserName();
    verify(this.loyaltyPointRepository).getLoyaltyPoints(username);
    verifyNoMoreInteractions(this.securityService, this.loyaltyPointRepository);
  }
}
