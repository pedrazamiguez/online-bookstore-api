package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.util.ObjectUtils;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {

  @Mapping(target = "lines", source = "items")
  Order toDomain(OrderEntity orderEntity);

  default Long mapOrderItemId(final OrderItemId value) {
    if (ObjectUtils.isEmpty(value)) {
      return null;
    }

    return value.getOrderId();
  }

  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "allocation.copies", source = "quantity")
  @Mapping(target = "allocation.book", source = "book")
  OrderItem orderItemEntityToOrderItem(OrderItemEntity orderItemEntity);

  default OrderEntity toNewOrderEntity(final CustomerEntity customerEntity) {
    final OrderEntity orderEntity = new OrderEntity();
    orderEntity.setCustomer(customerEntity);
    orderEntity.setStatus(OrderStatus.CREATED);
    orderEntity.setTotalPrice(BigDecimal.ZERO);
    return orderEntity;
  }

  default void patchWithExistingOrderItem(
      final OrderEntity orderEntity, final Long bookId, final Long quantity) {

    final Optional<OrderItemEntity> optionalItem =
        orderEntity.getItems().stream()
            .filter(item -> bookId.equals(item.getBook().getId()))
            .findFirst();

    optionalItem.ifPresent(
        existingItem -> existingItem.setQuantity(existingItem.getQuantity() + quantity));
  }

  default void patchWithNewOrderItem(
      final OrderEntity orderEntity, final BookEntity bookEntity, final Long quantity) {

    final List<OrderItemEntity> items = orderEntity.getItems();

    final OrderItemEntity newOrderItem = new OrderItemEntity();

    final OrderItemId orderItemId = new OrderItemId();
    orderItemId.setOrderId(orderEntity.getId());
    orderItemId.setLineNumber(items.size() + 1);

    newOrderItem.setId(orderItemId);
    newOrderItem.setOrder(orderEntity);
    newOrderItem.setBook(bookEntity);
    newOrderItem.setQuantity(quantity);

    items.add(newOrderItem);
  }
}
