package es.pedrazamiguez.api.onlinebookstore.domain.repository;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Customer;
import java.util.Optional;

public interface CustomerRepository {

  Optional<Customer> findCustomerByUsername(String username);
}
