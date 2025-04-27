package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.CustomerRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.loyalty.GetLoyaltyPointsUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.CustomerApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.LoyaltyPointsDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

    private final GetLoyaltyPointsUseCase getLoyaltyPointsUseCase;

    private final CustomerRestMapper customerRestMapper;

    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LoyaltyPointsDto> getCurrentCustomerLoyaltyPoints() {
        final Long points = this.getLoyaltyPointsUseCase.getCurrentCustomerLoyaltyPoints();
        final LoyaltyPointsDto loyaltyPointsDto =
                this.customerRestMapper.toLoyaltyPointsDto(points);
        return ResponseEntity.ok(loyaltyPointsDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> getCustomerLoyaltyPoints(final Long customerId) {
        throw new NotImplementedException("Not implemented yet");
    }
}
