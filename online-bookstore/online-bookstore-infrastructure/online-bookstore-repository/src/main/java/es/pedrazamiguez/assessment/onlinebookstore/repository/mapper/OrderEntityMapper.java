package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.*;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.util.ObjectUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {

  @Mapping(target = "lines", source = "items")
  Order toDomain(OrderEntity orderEntity);

  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "allocation.copies", source = "quantity")
  @Mapping(target = "allocation.book", source = "book")
  @Mapping(target = "payableAmount.discount", source = "purchasedDiscountRate")
  @Mapping(
      target = "payableAmount.subtotal",
      expression = "java( this.getSubTotal(orderItemEntity) )")
  OrderItem orderItemEntityToOrderItem(OrderItemEntity orderItemEntity);

  default BigDecimal getSubTotal(final OrderItemEntity orderItemEntity) {
    if (ObjectUtils.isEmpty(orderItemEntity)) {
      return BigDecimal.ZERO;
    }

    if (ObjectUtils.isEmpty(orderItemEntity.getPurchasedUnitPrice())) {
      return BigDecimal.ZERO;
    }

    return orderItemEntity
        .getPurchasedUnitPrice()
        .multiply(new BigDecimal(orderItemEntity.getQuantity()))
        .multiply(orderItemEntity.getPurchasedDiscountRate());
  }

  @Mapping(target = "order.id", source = "orderId")
  @Mapping(target = "quantity", source = "allocation.copies")
  @Mapping(target = "book", source = "allocation.book")
  @Mapping(target = "purchasedUnitPrice", source = "allocation.book.price")
  @Mapping(target = "purchasedDiscountRate", source = "payableAmount.discount")
  OrderItemEntity orderItemToOrderItemEntity(OrderItem orderItem);
}
