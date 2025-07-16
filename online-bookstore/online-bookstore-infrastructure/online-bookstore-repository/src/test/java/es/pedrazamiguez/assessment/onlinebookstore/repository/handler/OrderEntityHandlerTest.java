package es.pedrazamiguez.api.onlinebookstore.repository.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.api.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.api.onlinebookstore.domain.model.*;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderItemEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderEntityHandlerTest {

  @InjectMocks private OrderEntityHandler orderEntityHandler;

  @Test
  void givenCustomerEntity_whenCreateNewOrder_thenReturnOrderEntity() {
    // GIVEN
    final CustomerEntity customerEntity = Instancio.create(CustomerEntity.class);

    // WHEN
    final OrderEntity orderEntity = this.orderEntityHandler.createNewOrder(customerEntity);

    // THEN
    assertThat(orderEntity).isNotNull();
    assertThat(orderEntity.getCustomer()).isEqualTo(customerEntity);
    assertThat(orderEntity.getStatus()).isEqualTo(OrderStatus.CREATED);
    assertThat(orderEntity.getTotalPrice()).isZero();
  }

  @Test
  void givenPaymentAndShippingAddress_whenUpdateOrder_thenReturnUpdatedOrder() {
    // GIVEN
    final OrderEntity orderEntity =
        Instancio.of(OrderEntity.class)
            .ignore(field(OrderEntity::getPaymentMethod))
            .ignore(field(OrderEntity::getShippingAddress))
            .create();
    final PaymentMethod paymentMethod = Instancio.create(PaymentMethod.class);
    final String shippingAddress = Instancio.create(String.class);

    // WHEN
    this.orderEntityHandler.updateOrderPaymentAndShipping(
        orderEntity, paymentMethod, shippingAddress);

    // THEN
    assertThat(orderEntity.getPaymentMethod()).isEqualTo(paymentMethod);
    assertThat(orderEntity.getShippingAddress()).isEqualTo(shippingAddress);
  }

  @Test
  void givenOrderAndNewBook_whenAddToOrder_thenOrderContainsNewItem() {
    // GIVEN
    final OrderEntity orderEntity =
        Instancio.of(OrderEntity.class)
            .set(field(OrderEntity::getItems), new ArrayList<>())
            .create();
    final BookEntity bookEntity = Instancio.create(BookEntity.class);
    final Long quantity = 2L;

    // WHEN
    this.orderEntityHandler.addToOrder(orderEntity, bookEntity, quantity);

    // THEN
    assertThat(orderEntity.getItems()).hasSize(1);
    final OrderItemEntity addedItem = orderEntity.getItems().getFirst();
    assertThat(addedItem.getBook()).isEqualTo(bookEntity);
    assertThat(addedItem.getQuantity()).isEqualTo(quantity);
    assertThat(addedItem.getOrder()).isEqualTo(orderEntity);
    assertThat(addedItem.getId().getLineNumber()).isEqualTo(1);
  }

  @Test
  void givenOrderWithItem_whenRemoveFromOrder_thenQuantityReduced() {
    // GIVEN
    final BookEntity bookEntity = Instancio.create(BookEntity.class);
    final Long initialQuantity = 5L;
    final Long quantityToRemove = 2L;
    final OrderItemEntity orderItem =
        Instancio.of(OrderItemEntity.class)
            .set(field(OrderItemEntity::getBook), bookEntity)
            .set(field(OrderItemEntity::getQuantity), initialQuantity)
            .create();
    final OrderEntity orderEntity =
        Instancio.of(OrderEntity.class)
            .set(field(OrderEntity::getItems), List.of(orderItem))
            .create();

    // WHEN
    this.orderEntityHandler.removeFromOrder(orderEntity, bookEntity.getId(), quantityToRemove);

    // THEN
    assertThat(orderEntity.getItems()).hasSize(1);
    final OrderItemEntity updatedItem = orderEntity.getItems().getFirst();
    assertThat(updatedItem.getQuantity()).isEqualTo(initialQuantity - quantityToRemove);
  }

  @Test
  void givenOrderEntityAndDomainOrder_whenSyncOrderItems_thenItemsUpdated() {
    // GIVEN
    final BookEntity bookEntity = Instancio.create(BookEntity.class);
    final BigDecimal bookPrice = new BigDecimal("29.99");
    final BigDecimal discount = new BigDecimal("0.1");
    final OrderItemEntity orderItemEntity =
        Instancio.of(OrderItemEntity.class)
            .set(field(OrderItemEntity::getBook), bookEntity)
            .create();
    final OrderEntity orderEntity =
        Instancio.of(OrderEntity.class)
            .set(field(OrderEntity::getItems), List.of(orderItemEntity))
            .create();

    final Book domainBook =
        Instancio.of(Book.class)
            .set(field(Book::getId), bookEntity.getId())
            .set(field(Book::getPrice), bookPrice)
            .create();
    final BookAllocation allocation =
        Instancio.of(BookAllocation.class).set(field(BookAllocation::getBook), domainBook).create();
    final PayableAmount payableAmount =
        Instancio.of(PayableAmount.class).set(field(PayableAmount::getDiscount), discount).create();
    final OrderItem orderItem =
        Instancio.of(OrderItem.class)
            .set(field(OrderItem::getAllocation), allocation)
            .set(field(OrderItem::getPayableAmount), payableAmount)
            .create();
    final Order domainOrder =
        Instancio.of(Order.class).set(field(Order::getLines), List.of(orderItem)).create();

    // WHEN
    this.orderEntityHandler.syncOrderItemsWithDomain(orderEntity, domainOrder);

    // THEN
    assertThat(orderEntity.getItems()).hasSize(1);
    final OrderItemEntity updatedItem = orderEntity.getItems().getFirst();
    assertThat(updatedItem.getPurchasedUnitPrice()).isEqualTo(bookPrice);
    assertThat(updatedItem.getPurchasedDiscountRate()).isEqualTo(discount);
  }
}
