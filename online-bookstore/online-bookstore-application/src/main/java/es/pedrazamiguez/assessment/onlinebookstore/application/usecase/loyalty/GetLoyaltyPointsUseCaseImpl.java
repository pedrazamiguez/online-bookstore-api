package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.loyalty;

import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.LoyaltyPointRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.loyalty.GetLoyaltyPointsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetLoyaltyPointsUseCaseImpl implements GetLoyaltyPointsUseCase {

    private final SecurityService securityService;

    private final LoyaltyPointRepository loyaltyPointRepository;

    @Override
    public Long getCurrentCustomerLoyaltyPoints() {
        final String username = this.securityService.getCurrentUserName();
        log.info("Getting loyalty points for user: {}", username);

        return this.loyaltyPointRepository.getLoyaltyPoints(username);
    }
}
