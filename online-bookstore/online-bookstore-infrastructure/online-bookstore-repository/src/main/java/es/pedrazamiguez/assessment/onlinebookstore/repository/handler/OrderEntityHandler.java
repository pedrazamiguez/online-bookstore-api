package es.pedrazamiguez.api.onlinebookstore.repository.handler;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.api.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class OrderEntityHandler {

  public OrderEntity createNewOrder(final CustomerEntity customerEntity) {
    final OrderEntity orderEntity = new OrderEntity();
    orderEntity.setCustomer(customerEntity);
    orderEntity.setStatus(OrderStatus.CREATED);
    orderEntity.setTotalPrice(BigDecimal.ZERO);
    return orderEntity;
  }

  public void addToOrder(
      final OrderEntity orderEntity, final BookEntity bookEntity, final Long quantity) {
    final Optional<OrderItemEntity> existingItemOpt =
        orderEntity.getItems().stream()
            .filter(item -> bookEntity.getId().equals(item.getBook().getId()))
            .findFirst();

    if (existingItemOpt.isPresent()) {
      final OrderItemEntity existingItem = existingItemOpt.get();
      existingItem.setQuantity(existingItem.getQuantity() + quantity);
    } else {
      final OrderItemEntity newItem = new OrderItemEntity();
      final OrderItemId orderItemId = new OrderItemId();
      orderItemId.setOrderId(orderEntity.getId());
      orderItemId.setLineNumber(orderEntity.getItems().size() + 1);

      newItem.setId(orderItemId);
      newItem.setOrder(orderEntity);
      newItem.setBook(bookEntity);
      newItem.setQuantity(quantity);

      orderEntity.getItems().add(newItem);
    }
  }

  public void removeFromOrder(
      final OrderEntity orderEntity, final Long bookId, final Long quantity) {
    final Optional<OrderItemEntity> existingItemOpt =
        orderEntity.getItems().stream()
            .filter(item -> bookId.equals(item.getBook().getId()))
            .findFirst();

    existingItemOpt.ifPresent(
        existingItem -> {
          if (existingItem.getQuantity() > quantity) {
            existingItem.setQuantity(existingItem.getQuantity() - quantity);
          } else {
            orderEntity.getItems().remove(existingItem);
          }
        });
  }

  public void updateOrderPaymentAndShipping(
      final OrderEntity orderEntity,
      final PaymentMethod paymentMethod,
      final String shippingAddress) {
    orderEntity.setPaymentMethod(paymentMethod);
    orderEntity.setShippingAddress(shippingAddress);
  }

  public void syncOrderItemsWithDomain(final OrderEntity orderEntity, final Order order) {
    for (final OrderItemEntity itemEntity : orderEntity.getItems()) {
      final Long bookId = itemEntity.getBook().getId();

      final OrderItem matchingDomainItem =
          order.getLines().stream()
              .filter(line -> line.getAllocation().getBook().getId().equals(bookId))
              .findFirst()
              .orElse(null);

      if (!ObjectUtils.isEmpty(matchingDomainItem)) {
        itemEntity.setPurchasedUnitPrice(matchingDomainItem.getAllocation().getBook().getPrice());
        itemEntity.setPurchasedDiscountRate(matchingDomainItem.getPayableAmount().getDiscount());
      }
    }
  }
}
