package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Customer;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.CustomerRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.CustomerEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

  private final CustomerJpaRepository customerJpaRepository;

  private final CustomerEntityMapper customerEntityMapper;

  @Override
  public Optional<Customer> findCustomerByUsername(final String username) {
    log.info("Finding customer by username: {}", username);
    return this.customerJpaRepository
        .findByUsername(username)
        .map(this.customerEntityMapper::toDomain);
  }
}
