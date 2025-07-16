package es.pedrazamiguez.onlinebookstore.application.service.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.model.OrderItem;
import java.util.Arrays;
import java.util.Collections;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoyaltyPointsServiceImplTest {

  @InjectMocks private LoyaltyPointsServiceImpl loyaltyPointsService;

  @Test
  @DisplayName("calculateLoyaltyPoints returns sum of copies for order with multiple lines")
  void givenOrderWithMultipleLines_whenCalculateLoyaltyPoints_thenReturnSumOfCopies() {
    // GIVEN
    final Order order = Instancio.create(Order.class);
    final OrderItem line1 =
        Instancio.of(OrderItem.class)
            .set(
                Select.field("allocation"),
                Instancio.of(BookAllocation.class).set(Select.field("copies"), 3L).create())
            .create();
    final OrderItem line2 =
        Instancio.of(OrderItem.class)
            .set(
                Select.field("allocation"),
                Instancio.of(BookAllocation.class).set(Select.field("copies"), 2L).create())
            .create();
    order.setLines(Arrays.asList(line1, line2));

    // WHEN
    final Long result = this.loyaltyPointsService.calculateLoyaltyPoints(order);

    // THEN
    assertThat(result).isEqualTo(5L);
  }

  @Test
  @DisplayName("calculateLoyaltyPoints returns copies for order with single line")
  void givenOrderWithSingleLine_whenCalculateLoyaltyPoints_thenReturnCopies() {
    // GIVEN
    final Order order = Instancio.create(Order.class);
    final OrderItem line =
        Instancio.of(OrderItem.class)
            .set(
                Select.field("allocation"),
                Instancio.of(BookAllocation.class).set(Select.field("copies"), 7L).create())
            .create();
    order.setLines(Collections.singletonList(line));

    // WHEN
    final Long result = this.loyaltyPointsService.calculateLoyaltyPoints(order);

    // THEN
    assertThat(result).isEqualTo(7L);
  }

  @Test
  @DisplayName("calculateLoyaltyPoints returns zero for order with empty lines")
  void givenOrderWithEmptyLines_whenCalculateLoyaltyPoints_thenReturnZero() {
    // GIVEN
    final Order order =
        Instancio.of(Order.class).set(Select.field("lines"), Collections.emptyList()).create();

    // WHEN
    final Long result = this.loyaltyPointsService.calculateLoyaltyPoints(order);

    // THEN
    assertThat(result).isZero();
  }

  @Test
  @DisplayName("calculateLoyaltyPoints returns zero for order with zero copies")
  void givenOrderWithZeroCopies_whenCalculateLoyaltyPoints_thenReturnZero() {
    // GIVEN
    final Order order = Instancio.create(Order.class);
    final OrderItem line1 =
        Instancio.of(OrderItem.class)
            .set(
                Select.field("allocation"),
                Instancio.of(BookAllocation.class).set(Select.field("copies"), 0L).create())
            .create();
    final OrderItem line2 =
        Instancio.of(OrderItem.class)
            .set(
                Select.field("allocation"),
                Instancio.of(BookAllocation.class).set(Select.field("copies"), 0L).create())
            .create();
    order.setLines(Arrays.asList(line1, line2));

    // WHEN
    final Long result = this.loyaltyPointsService.calculateLoyaltyPoints(order);

    // THEN
    assertThat(result).isZero();
  }

  @Test
  @DisplayName("calculateLoyaltyPoints throws NullPointerException for order with null lines")
  void givenOrderWithNullLines_whenCalculateLoyaltyPoints_thenThrowNullPointerException() {
    // GIVEN
    final Order order = Instancio.of(Order.class).set(Select.field("lines"), null).create();

    // WHEN / THEN
    assertThatThrownBy(() -> this.loyaltyPointsService.calculateLoyaltyPoints(order))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("calculateLoyaltyPoints throws NullPointerException for order with null allocation")
  void givenOrderWithNullAllocation_whenCalculateLoyaltyPoints_thenThrowNullPointerException() {
    // GIVEN
    final Order order = Instancio.create(Order.class);
    final OrderItem line =
        Instancio.of(OrderItem.class).set(Select.field("allocation"), null).create();
    order.setLines(Collections.singletonList(line));

    // WHEN / THEN
    assertThatThrownBy(() -> this.loyaltyPointsService.calculateLoyaltyPoints(order))
        .isInstanceOf(NullPointerException.class);
  }
}
