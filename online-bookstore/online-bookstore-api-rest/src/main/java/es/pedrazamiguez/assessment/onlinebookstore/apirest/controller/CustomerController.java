package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.CustomerApi;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

  @Override
  public ResponseEntity<Void> getCurrentCustomerLoyaltyPoints() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  public ResponseEntity<Void> getCustomerLoyaltyPoints(final Long customerId) {
    throw new NotImplementedException("Not implemented yet");
  }
}
