package es.pedrazamiguez.onlinebookstore.domain.repository;

import es.pedrazamiguez.onlinebookstore.domain.model.Customer;
import java.util.Optional;

public interface CustomerRepository {

  Optional<Customer> findCustomerByUsername(String username);
}
