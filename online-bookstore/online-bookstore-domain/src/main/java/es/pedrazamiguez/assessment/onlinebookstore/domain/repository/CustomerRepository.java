package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Customer;
import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findCustomerByUsername(String username);
}
