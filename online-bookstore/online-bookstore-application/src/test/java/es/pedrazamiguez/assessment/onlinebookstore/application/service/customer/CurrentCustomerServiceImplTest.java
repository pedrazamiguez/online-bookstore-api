package es.pedrazamiguez.assessment.onlinebookstore.application.service.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Customer;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.CustomerRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrentCustomerServiceImplTest {

  @InjectMocks private CurrentCustomerServiceImpl currentCustomerService;

  @Mock private SecurityService securityService;
  @Mock private CustomerRepository customerRepository;

  @Test
  void givenUsername_whenGetCurrentCustomer_thenReturnCustomer() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Customer customer =
        Instancio.of(Customer.class).set(field(Customer::getUsername), username).create();

    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.customerRepository.findCustomerByUsername(username))
        .thenReturn(Optional.of(customer));

    // WHEN
    final Customer result = this.currentCustomerService.getCurrentCustomer();

    // THEN
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo(username);

    verify(this.securityService).getCurrentUserName();
    verify(this.customerRepository).findCustomerByUsername(username);
    verifyNoMoreInteractions(this.securityService, this.customerRepository);
  }

  @Test
  void givenNonExistingUsername_whenGetCurrentCustomer_thenThrowCustomerNotFoundException() {
    // GIVEN
    final String username = Instancio.create(String.class);

    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.customerRepository.findCustomerByUsername(username)).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> this.currentCustomerService.getCurrentCustomer())
        .isInstanceOf(CustomerNotFoundException.class)
        .hasMessage("Customer with username %s not found", username);

    // THEN
    verify(this.securityService).getCurrentUserName();
    verify(this.customerRepository).findCustomerByUsername(username);
    verifyNoMoreInteractions(this.securityService, this.customerRepository);
  }
}
