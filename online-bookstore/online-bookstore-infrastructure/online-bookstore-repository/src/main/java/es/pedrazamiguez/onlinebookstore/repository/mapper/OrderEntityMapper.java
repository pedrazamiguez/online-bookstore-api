package es.pedrazamiguez.onlinebookstore.repository.mapper;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.onlinebookstore.repository.entity.*;
import es.pedrazamiguez.onlinebookstore.repository.helper.OrderCalculationHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    imports = OrderCalculationHelper.class)
public interface OrderEntityMapper {

  @Mapping(target = "lines", source = "items")
  Order toDomain(OrderEntity orderEntity);

  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "allocation.copies", source = "quantity")
  @Mapping(target = "allocation.book", source = "book")
  @Mapping(target = "payableAmount.discount", source = "purchasedDiscountRate")
  @Mapping(
      target = "payableAmount.subtotal",
      expression = "java( OrderCalculationHelper.calculateSubtotal(orderItemEntity) )")
  OrderItem orderItemEntityToOrderItem(OrderItemEntity orderItemEntity);

  @Mapping(target = "order.id", source = "orderId")
  @Mapping(target = "quantity", source = "allocation.copies")
  @Mapping(target = "book", source = "allocation.book")
  @Mapping(target = "purchasedUnitPrice", source = "allocation.book.price")
  @Mapping(target = "purchasedDiscountRate", source = "payableAmount.discount")
  OrderItemEntity orderItemToOrderItemEntity(OrderItem orderItem);
}
