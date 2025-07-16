package es.pedrazamiguez.onlinebookstore.application.usecase.order;

import static org.mockito.Mockito.*;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.onlinebookstore.domain.service.security.SecurityService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClearOrderUseCaseImplTest {

  @InjectMocks private ClearOrderUseCaseImpl clearOrderUseCase;

  @Mock private SecurityService securityService;

  @Mock private CurrentOrderService currentOrderService;

  @Mock private OrderRepository orderRepository;

  @Test
  void givenExistingOrder_whenClearOrderItems_thenDeleteOrderItems() {
    // GIVEN
    final String username = "user123";
    final Order existingOrder = Instancio.create(Order.class);

    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.currentOrderService.getOrCreateOrder(username)).thenReturn(existingOrder);

    // WHEN
    this.clearOrderUseCase.clearOrderItems();

    // THEN
    verify(this.securityService).getCurrentUserName();
    verify(this.currentOrderService).getOrCreateOrder(username);
    verify(this.orderRepository).deleteOrderItems(existingOrder.getId());
    verifyNoMoreInteractions(this.securityService, this.currentOrderService, this.orderRepository);
  }
}
