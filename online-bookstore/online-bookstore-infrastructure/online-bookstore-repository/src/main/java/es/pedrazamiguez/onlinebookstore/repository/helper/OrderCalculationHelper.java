package es.pedrazamiguez.onlinebookstore.repository.helper;

import es.pedrazamiguez.onlinebookstore.repository.entity.OrderItemEntity;
import java.math.BigDecimal;
import org.springframework.util.ObjectUtils;

public class OrderCalculationHelper {

  private OrderCalculationHelper() {}

  public static BigDecimal calculateSubtotal(final OrderItemEntity orderItemEntity) {
    if (ObjectUtils.isEmpty(orderItemEntity)
        || ObjectUtils.isEmpty(orderItemEntity.getPurchasedUnitPrice())
        || orderItemEntity.getQuantity() == null
        || orderItemEntity.getPurchasedDiscountRate() == null) {
      return BigDecimal.ZERO;
    }

    return orderItemEntity
        .getPurchasedUnitPrice()
        .multiply(BigDecimal.valueOf(orderItemEntity.getQuantity()))
        .multiply(orderItemEntity.getPurchasedDiscountRate());
  }
}
