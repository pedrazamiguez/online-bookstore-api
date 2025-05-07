package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Customer;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.CustomerEntityMapper;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryImplTest {

  @InjectMocks private CustomerRepositoryImpl customerRepository;

  @Mock private CustomerJpaRepository customerJpaRepository;

  @Mock private CustomerEntityMapper customerEntityMapper;

  @Test
  void givenUsername_whenFindCustomerByUsername_thenReturnCustomer() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final CustomerEntity customerEntity =
        Instancio.of(CustomerEntity.class)
            .set(field(CustomerEntity::getUsername), username)
            .create();
    final Customer customer =
        Instancio.of(Customer.class).set(field(Customer::getUsername), username).create();

    when(this.customerJpaRepository.findByUsername(username))
        .thenReturn(Optional.of(customerEntity));

    when(this.customerEntityMapper.toDomain(customerEntity)).thenReturn(customer);

    when(this.customerJpaRepository.findByUsername(username))
        .thenReturn(Optional.of(customerEntity));

    when(this.customerEntityMapper.toDomain(customerEntity)).thenReturn(customer);

    // WHEN
    final Optional<Customer> result = this.customerRepository.findCustomerByUsername(username);

    // THEN
    assertThat(result).isPresent().contains(customer);
    assertThat(result.get().getUsername()).isEqualTo(username);

    verify(this.customerJpaRepository).findByUsername(username);
    verify(this.customerEntityMapper).toDomain(customerEntity);
    verifyNoMoreInteractions(this.customerJpaRepository, this.customerEntityMapper);
  }

  @Test
  void givenNonExistingUsername_whenFindCustomerByUsername_thenReturnEmpty() {
    // GIVEN
    final String username = Instancio.create(String.class);

    when(this.customerJpaRepository.findByUsername(username)).thenReturn(Optional.empty());

    // WHEN
    final Optional<Customer> result = this.customerRepository.findCustomerByUsername(username);

    // THEN
    assertThat(result).isNotPresent();

    verify(this.customerJpaRepository).findByUsername(username);
    verifyNoMoreInteractions(this.customerJpaRepository);
  }
}
