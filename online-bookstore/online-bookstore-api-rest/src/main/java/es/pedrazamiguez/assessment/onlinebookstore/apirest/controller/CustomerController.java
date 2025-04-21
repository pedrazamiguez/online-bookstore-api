package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.CustomerApi;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> getCurrentCustomerLoyaltyPoints() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> getCustomerLoyaltyPoints(final Long customerId) {
    throw new NotImplementedException("Not implemented yet");
  }
}
