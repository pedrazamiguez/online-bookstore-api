package es.pedrazamiguez.assessment.onlinebookstore.repository.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.OrderItemEntity;
import java.math.BigDecimal;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class OrderCalculationHelperTest {

  @Test
  void should_return_zero_when_orderItemEntity_is_null() {
    // GIVEN
    final OrderItemEntity orderItemEntity = null;

    // WHEN
    final BigDecimal subtotal = OrderCalculationHelper.calculateSubtotal(orderItemEntity);

    // THEN
    assertThat(subtotal).isZero();
  }

  @Test
  void should_return_zero_when_unitPrice_is_null() {
    // GIVEN
    final OrderItemEntity orderItemEntity =
        Instancio.of(OrderItemEntity.class)
            .ignore(field(OrderItemEntity::getPurchasedUnitPrice))
            .create();

    // WHEN
    final BigDecimal subtotal = OrderCalculationHelper.calculateSubtotal(orderItemEntity);

    // THEN
    assertThat(subtotal).isZero();
  }

  @Test
  void should_calculate_correct_subtotal() {
    // GIVEN
    final BigDecimal price = new BigDecimal("20.00");
    final Long quantity = 3L;
    final BigDecimal discount = new BigDecimal("0.85");

    final OrderItemEntity orderItemEntity =
        Instancio.of(OrderItemEntity.class)
            .set(field(OrderItemEntity::getPurchasedUnitPrice), price)
            .set(field(OrderItemEntity::getQuantity), quantity)
            .set(field(OrderItemEntity::getPurchasedDiscountRate), discount)
            .create();

    // WHEN
    final BigDecimal subtotal = OrderCalculationHelper.calculateSubtotal(orderItemEntity);

    // THEN
    assertThat(subtotal).isEqualByComparingTo(new BigDecimal("51.00")); // 20 * 3 * 0.85
  }
}
