package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrentOrderServiceImplTest {

  @InjectMocks private CurrentOrderServiceImpl currentOrderService;

  @Mock private OrderRepository orderRepository;

  @Test
  void givenUsername_whenGetOrCreateOrder_thenReturnExistingOrder() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Order existingOrder = Instancio.create(Order.class);

    when(this.orderRepository.findCreatedOrderForCustomer(username))
        .thenReturn(Optional.of(existingOrder));

    // WHEN
    final Order result = this.currentOrderService.getOrCreateOrder(username);

    // THEN
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(existingOrder.getId());

    verify(this.orderRepository).findCreatedOrderForCustomer(username);
    verify(this.orderRepository, never()).createNewOrder(username);
    verifyNoMoreInteractions(this.orderRepository);
  }

  @Test
  void givenUsername_whenGetOrCreateOrder_thenCreateNewOrder() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Order newOrder = Instancio.create(Order.class);

    when(this.orderRepository.findCreatedOrderForCustomer(username)).thenReturn(Optional.empty());
    when(this.orderRepository.createNewOrder(username)).thenReturn(newOrder);

    // WHEN
    final Order result = this.currentOrderService.getOrCreateOrder(username);

    // THEN
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(newOrder.getId());

    verify(this.orderRepository).findCreatedOrderForCustomer(username);
    verify(this.orderRepository).createNewOrder(username);
    verifyNoMoreInteractions(this.orderRepository);
  }
}
