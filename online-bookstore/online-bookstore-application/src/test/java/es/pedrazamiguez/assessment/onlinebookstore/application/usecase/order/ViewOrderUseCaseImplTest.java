package es.pedrazamiguez.api.onlinebookstore.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.security.SecurityService;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ViewOrderUseCaseImplTest {

  @InjectMocks private ViewOrderUseCaseImpl viewOrderUseCase;

  @Mock private SecurityService securityService;

  @Mock private CurrentOrderService currentOrderService;

  @Mock private FinalPriceService finalPriceService;

  @Test
  void givenOrderWithLines_whenGetCurrentOrderForCustomer_thenReturnsOrder() {
    // GIVEN
    final String username = "user123";
    final Order order = new Order();
    order.setLines(List.of(new OrderItem())); // Non-empty lines
    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.currentOrderService.getOrCreateOrder(username)).thenReturn(order);
    doAnswer(
            invocation -> {
              final Order arg = invocation.getArgument(0);
              arg.setTotalPrice(new BigDecimal("100.0")); // Simulate price calculation
              return null;
            })
        .when(this.finalPriceService)
        .calculate(any(Order.class));

    // WHEN
    final Optional<Order> result = this.viewOrderUseCase.getCurrentOrderForCustomer();

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get()).isSameAs(order);
    assertThat(result.get().getTotalPrice()).isEqualByComparingTo(new BigDecimal("100.0"));
    verify(this.securityService).getCurrentUserName();
    verify(this.currentOrderService).getOrCreateOrder(username);
    verify(this.finalPriceService).calculate(order);
  }

  @Test
  void givenEmptyOrderLines_whenGetCurrentOrderForCustomer_thenReturnsEmptyOptional() {
    // GIVEN
    final String username = "user123";
    final Order order = new Order();
    order.setLines(Collections.emptyList()); // Empty lines
    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.currentOrderService.getOrCreateOrder(username)).thenReturn(order);

    // WHEN
    final Optional<Order> result = this.viewOrderUseCase.getCurrentOrderForCustomer();

    // THEN
    assertThat(result).isEmpty();
    verify(this.securityService).getCurrentUserName();
    verify(this.currentOrderService).getOrCreateOrder(username);
    verify(this.finalPriceService, never()).calculate(any(Order.class));
  }

  @Test
  void givenNullOrderLines_whenGetCurrentOrderForCustomer_thenReturnsEmptyOptional() {
    // GIVEN
    final String username = "user123";
    final Order order = new Order();
    order.setLines(null); // Null lines
    when(this.securityService.getCurrentUserName()).thenReturn(username);
    when(this.currentOrderService.getOrCreateOrder(username)).thenReturn(order);

    // WHEN
    final Optional<Order> result = this.viewOrderUseCase.getCurrentOrderForCustomer();

    // THEN
    assertThat(result).isEmpty();
    verify(this.securityService).getCurrentUserName();
    verify(this.currentOrderService).getOrCreateOrder(username);
    verify(this.finalPriceService, never()).calculate(any(Order.class));
  }
}
