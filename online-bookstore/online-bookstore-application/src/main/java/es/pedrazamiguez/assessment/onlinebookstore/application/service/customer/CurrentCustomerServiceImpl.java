package es.pedrazamiguez.api.onlinebookstore.application.service.customer;

import es.pedrazamiguez.api.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Customer;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.CustomerRepository;
import es.pedrazamiguez.api.onlinebookstore.domain.service.customer.CurrentCustomerService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentCustomerServiceImpl implements CurrentCustomerService {

  private final SecurityService securityService;

  private final CustomerRepository customerRepository;

  @Override
  public Customer getCurrentCustomer() {
    final String username = this.securityService.getCurrentUserName();
    return this.customerRepository
        .findCustomerByUsername(username)
        .orElseThrow(() -> new CustomerNotFoundException(username));
  }
}
