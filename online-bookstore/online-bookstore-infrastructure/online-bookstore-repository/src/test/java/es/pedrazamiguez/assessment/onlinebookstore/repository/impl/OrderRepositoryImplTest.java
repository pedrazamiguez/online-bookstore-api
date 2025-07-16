package es.pedrazamiguez.api.onlinebookstore.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.api.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotInOrderException;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderItemEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.handler.OrderEntityHandler;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.OrderJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.mapper.OrderEntityMapper;
import java.util.Collections;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

  @InjectMocks private OrderRepositoryImpl orderRepository;

  @Mock private OrderJpaRepository orderJpaRepository;

  @Mock private CustomerJpaRepository customerJpaRepository;

  @Mock private BookJpaRepository bookJpaRepository;

  @Mock private OrderEntityMapper orderEntityMapper;

  @Mock private OrderEntityHandler orderEntityHandler;

  private String username;
  private Long orderId;
  private Long bookId;
  private Long quantity;
  private CustomerEntity customerEntity;
  private OrderEntity orderEntity;
  private BookEntity bookEntity;
  private Order order;

  @BeforeEach
  void setUp() {
    this.username = Instancio.create(String.class);
    this.orderId = Instancio.create(Long.class);
    this.bookId = Instancio.create(Long.class);
    this.quantity = 3L;
    this.customerEntity =
        Instancio.of(CustomerEntity.class)
            .set(field(CustomerEntity::getUsername), this.username)
            .create();
    this.orderEntity =
        Instancio.of(OrderEntity.class)
            .set(field(OrderEntity::getId), this.orderId)
            .set(field(OrderEntity::getCustomer), this.customerEntity)
            .create();
    this.bookEntity =
        Instancio.of(BookEntity.class).set(field(BookEntity::getId), this.bookId).create();
    this.order = Instancio.of(Order.class).set(field(Order::getId), this.orderId).create();
  }

  @Nested
  @DisplayName("Tests for findCreatedOrderForCustomer")
  class FindCreatedOrderForCustomerTests {

    @Test
    @DisplayName("findCreatedOrderForCustomer returns order when found")
    void shouldReturnOrderWhenFound() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository
              .findOneByCustomer_usernameAndStatusOrderByUpdatedAtDesc(
                  OrderRepositoryImplTest.this.username, OrderStatus.CREATED))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));
      when(OrderRepositoryImplTest.this.orderEntityMapper.toDomain(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.order);

      // WHEN
      final Optional<Order> result =
          OrderRepositoryImplTest.this.orderRepository.findCreatedOrderForCustomer(
              OrderRepositoryImplTest.this.username);

      // THEN
      assertThat(result).isPresent().contains(OrderRepositoryImplTest.this.order);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findOneByCustomer_usernameAndStatusOrderByUpdatedAtDesc(
              OrderRepositoryImplTest.this.username, OrderStatus.CREATED);
      verify(OrderRepositoryImplTest.this.orderEntityMapper)
          .toDomain(OrderRepositoryImplTest.this.orderEntity);
      verifyNoMoreInteractions(
          OrderRepositoryImplTest.this.orderJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("findCreatedOrderForCustomer returns empty when no order found")
    void shouldReturnEmptyWhenNoOrderFound() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository
              .findOneByCustomer_usernameAndStatusOrderByUpdatedAtDesc(
                  OrderRepositoryImplTest.this.username, OrderStatus.CREATED))
          .thenReturn(Optional.empty());

      // WHEN
      final Optional<Order> result =
          OrderRepositoryImplTest.this.orderRepository.findCreatedOrderForCustomer(
              OrderRepositoryImplTest.this.username);

      // THEN
      assertThat(result).isEmpty();
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findOneByCustomer_usernameAndStatusOrderByUpdatedAtDesc(
              OrderRepositoryImplTest.this.username, OrderStatus.CREATED);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.orderEntityMapper,
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository);
    }
  }

  @Nested
  @DisplayName("Tests for createNewOrder")
  class CreateNewOrderTests {

    @Test
    @DisplayName("createNewOrder creates and returns new order")
    void shouldCreateAndReturnNewOrder() {
      // GIVEN
      final OrderEntity newOrderEntity = Instancio.create(OrderEntity.class);
      when(OrderRepositoryImplTest.this.customerJpaRepository.findByUsername(
              OrderRepositoryImplTest.this.username))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.customerEntity));
      when(OrderRepositoryImplTest.this.orderEntityHandler.createNewOrder(
              OrderRepositoryImplTest.this.customerEntity))
          .thenReturn(newOrderEntity);
      when(OrderRepositoryImplTest.this.orderJpaRepository.save(newOrderEntity))
          .thenReturn(OrderRepositoryImplTest.this.orderEntity);
      when(OrderRepositoryImplTest.this.orderEntityMapper.toDomain(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.order);

      // WHEN
      final Order result =
          OrderRepositoryImplTest.this.orderRepository.createNewOrder(
              OrderRepositoryImplTest.this.username);

      // THEN
      assertThat(result).isEqualTo(OrderRepositoryImplTest.this.order);
      verify(OrderRepositoryImplTest.this.customerJpaRepository)
          .findByUsername(OrderRepositoryImplTest.this.username);
      verify(OrderRepositoryImplTest.this.orderEntityHandler)
          .createNewOrder(OrderRepositoryImplTest.this.customerEntity);
      verify(OrderRepositoryImplTest.this.orderJpaRepository).save(newOrderEntity);
      verify(OrderRepositoryImplTest.this.orderEntityMapper)
          .toDomain(OrderRepositoryImplTest.this.orderEntity);
      verifyNoMoreInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.orderJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
      verifyNoInteractions(OrderRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("createNewOrder throws CustomerNotFoundException when customer not found")
    void shouldThrowCustomerNotFoundExceptionWhenCustomerNotFound() {
      // GIVEN
      when(OrderRepositoryImplTest.this.customerJpaRepository.findByUsername(
              OrderRepositoryImplTest.this.username))
          .thenReturn(Optional.empty());

      // WHEN & THEN
      assertThatThrownBy(
              () ->
                  OrderRepositoryImplTest.this.orderRepository.createNewOrder(
                      OrderRepositoryImplTest.this.username))
          .isInstanceOf(CustomerNotFoundException.class)
          .hasMessageContaining(OrderRepositoryImplTest.this.username);

      verify(OrderRepositoryImplTest.this.customerJpaRepository)
          .findByUsername(OrderRepositoryImplTest.this.username);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.orderJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper,
          OrderRepositoryImplTest.this.bookJpaRepository);
    }
  }

  @Nested
  @DisplayName("Tests for saveOrderItem")
  class SaveOrderItemTests {

    @Test
    @DisplayName("saveOrderItem adds new item when book not in order")
    void shouldAddNewItemWhenBookNotInOrder() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));
      when(OrderRepositoryImplTest.this.bookJpaRepository.findById(
              OrderRepositoryImplTest.this.bookId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.bookEntity));
      doNothing()
          .when(OrderRepositoryImplTest.this.orderEntityHandler)
          .addToOrder(
              OrderRepositoryImplTest.this.orderEntity,
              OrderRepositoryImplTest.this.bookEntity,
              OrderRepositoryImplTest.this.quantity);
      when(OrderRepositoryImplTest.this.orderJpaRepository.save(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.orderEntity);
      when(OrderRepositoryImplTest.this.orderEntityMapper.toDomain(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.order);

      // Simulate book not in order
      OrderRepositoryImplTest.this.orderEntity.setItems(Collections.emptyList());

      // WHEN
      final Order result =
          OrderRepositoryImplTest.this.orderRepository.saveOrderItem(
              OrderRepositoryImplTest.this.orderId,
              OrderRepositoryImplTest.this.bookId,
              OrderRepositoryImplTest.this.quantity);

      // THEN
      assertThat(result).isEqualTo(OrderRepositoryImplTest.this.order);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verify(OrderRepositoryImplTest.this.bookJpaRepository)
          .findById(OrderRepositoryImplTest.this.bookId);
      verify(OrderRepositoryImplTest.this.orderEntityHandler)
          .addToOrder(
              OrderRepositoryImplTest.this.orderEntity,
              OrderRepositoryImplTest.this.bookEntity,
              OrderRepositoryImplTest.this.quantity);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .save(OrderRepositoryImplTest.this.orderEntity);
      verify(OrderRepositoryImplTest.this.orderEntityMapper)
          .toDomain(OrderRepositoryImplTest.this.orderEntity);
      verifyNoMoreInteractions(
          OrderRepositoryImplTest.this.orderJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
      verifyNoInteractions(OrderRepositoryImplTest.this.customerJpaRepository);
    }

    @Test
    @DisplayName("saveOrderItem updates existing item when book in order")
    void shouldUpdateExistingItemWhenBookInOrder() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));
      doNothing()
          .when(OrderRepositoryImplTest.this.orderEntityHandler)
          .addToOrder(
              OrderRepositoryImplTest.this.orderEntity,
              OrderRepositoryImplTest.this.bookEntity,
              OrderRepositoryImplTest.this.quantity);
      when(OrderRepositoryImplTest.this.orderJpaRepository.save(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.orderEntity);
      when(OrderRepositoryImplTest.this.orderEntityMapper.toDomain(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.order);

      // Simulate book in order
      final OrderItemEntity orderItem =
          Instancio.of(OrderItemEntity.class)
              .set(field(OrderItemEntity::getBook), OrderRepositoryImplTest.this.bookEntity)
              .create();
      OrderRepositoryImplTest.this.orderEntity.setItems(Collections.singletonList(orderItem));

      // WHEN
      final Order result =
          OrderRepositoryImplTest.this.orderRepository.saveOrderItem(
              OrderRepositoryImplTest.this.orderId,
              OrderRepositoryImplTest.this.bookId,
              OrderRepositoryImplTest.this.quantity);

      // THEN
      assertThat(result).isEqualTo(OrderRepositoryImplTest.this.order);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verify(OrderRepositoryImplTest.this.orderEntityHandler)
          .addToOrder(
              OrderRepositoryImplTest.this.orderEntity,
              OrderRepositoryImplTest.this.bookEntity,
              OrderRepositoryImplTest.this.quantity);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .save(OrderRepositoryImplTest.this.orderEntity);
      verify(OrderRepositoryImplTest.this.orderEntityMapper)
          .toDomain(OrderRepositoryImplTest.this.orderEntity);
      verifyNoMoreInteractions(
          OrderRepositoryImplTest.this.orderJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("saveOrderItem throws OrderNotFoundException when order not found")
    void shouldThrowOrderNotFoundExceptionWhenOrderNotFound() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.empty());

      // WHEN & THEN
      assertThatThrownBy(
              () ->
                  OrderRepositoryImplTest.this.orderRepository.saveOrderItem(
                      OrderRepositoryImplTest.this.orderId,
                      OrderRepositoryImplTest.this.bookId,
                      OrderRepositoryImplTest.this.quantity))
          .isInstanceOf(OrderNotFoundException.class)
          .hasMessageContaining(OrderRepositoryImplTest.this.orderId.toString());

      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
    }

    @Test
    @DisplayName("saveOrderItem throws BookNotFoundException when book not found")
    void shouldThrowBookNotFoundExceptionWhenBookNotFound() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));
      when(OrderRepositoryImplTest.this.bookJpaRepository.findById(
              OrderRepositoryImplTest.this.bookId))
          .thenReturn(Optional.empty());

      // Simulate book not in order
      OrderRepositoryImplTest.this.orderEntity.setItems(Collections.emptyList());

      // WHEN & THEN
      assertThatThrownBy(
              () ->
                  OrderRepositoryImplTest.this.orderRepository.saveOrderItem(
                      OrderRepositoryImplTest.this.orderId,
                      OrderRepositoryImplTest.this.bookId,
                      OrderRepositoryImplTest.this.quantity))
          .isInstanceOf(BookNotFoundException.class)
          .hasMessageContaining(OrderRepositoryImplTest.this.bookId.toString());

      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verify(OrderRepositoryImplTest.this.bookJpaRepository)
          .findById(OrderRepositoryImplTest.this.bookId);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
    }
  }

  @Nested
  @DisplayName("Tests for deleteOrderItem")
  class DeleteOrderItemTests {

    @Test
    @DisplayName("deleteOrderItem removes item when book in order")
    void shouldRemoveItemWhenBookInOrder() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));
      doNothing()
          .when(OrderRepositoryImplTest.this.orderEntityHandler)
          .removeFromOrder(
              OrderRepositoryImplTest.this.orderEntity,
              OrderRepositoryImplTest.this.bookId,
              OrderRepositoryImplTest.this.quantity);
      when(OrderRepositoryImplTest.this.orderJpaRepository.save(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.orderEntity);
      when(OrderRepositoryImplTest.this.orderEntityMapper.toDomain(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.order);

      // Simulate book in order
      final OrderItemEntity orderItem =
          Instancio.of(OrderItemEntity.class)
              .set(field(OrderItemEntity::getBook), OrderRepositoryImplTest.this.bookEntity)
              .create();
      OrderRepositoryImplTest.this.orderEntity.setItems(Collections.singletonList(orderItem));

      // WHEN
      final Order result =
          OrderRepositoryImplTest.this.orderRepository.deleteOrderItem(
              OrderRepositoryImplTest.this.orderId,
              OrderRepositoryImplTest.this.bookId,
              OrderRepositoryImplTest.this.quantity);

      // THEN
      assertThat(result).isEqualTo(OrderRepositoryImplTest.this.order);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verify(OrderRepositoryImplTest.this.orderEntityHandler)
          .removeFromOrder(
              OrderRepositoryImplTest.this.orderEntity,
              OrderRepositoryImplTest.this.bookId,
              OrderRepositoryImplTest.this.quantity);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .save(OrderRepositoryImplTest.this.orderEntity);
      verify(OrderRepositoryImplTest.this.orderEntityMapper)
          .toDomain(OrderRepositoryImplTest.this.orderEntity);
      verifyNoMoreInteractions(
          OrderRepositoryImplTest.this.orderJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("deleteOrderItem throws OrderNotFoundException when order not found")
    void shouldThrowOrderNotFoundExceptionWhenOrderNotFound() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.empty());

      // WHEN & THEN
      assertThatThrownBy(
              () ->
                  OrderRepositoryImplTest.this.orderRepository.deleteOrderItem(
                      OrderRepositoryImplTest.this.orderId,
                      OrderRepositoryImplTest.this.bookId,
                      OrderRepositoryImplTest.this.quantity))
          .isInstanceOf(OrderNotFoundException.class)
          .hasMessageContaining(OrderRepositoryImplTest.this.orderId.toString());

      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
    }

    @Test
    @DisplayName("deleteOrderItem throws BookNotInOrderException when book not in order")
    void shouldThrowBookNotInOrderExceptionWhenBookNotInOrder() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));

      // Simulate book not in order
      OrderRepositoryImplTest.this.orderEntity.setItems(Collections.emptyList());

      // WHEN & THEN
      assertThatThrownBy(
              () ->
                  OrderRepositoryImplTest.this.orderRepository.deleteOrderItem(
                      OrderRepositoryImplTest.this.orderId,
                      OrderRepositoryImplTest.this.bookId,
                      OrderRepositoryImplTest.this.quantity))
          .isInstanceOf(BookNotInOrderException.class)
          .hasMessageContaining(
              OrderRepositoryImplTest.this.bookId.toString(),
              OrderRepositoryImplTest.this.orderId.toString());

      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
    }
  }

  @Nested
  @DisplayName("Tests for deleteOrderItems")
  class DeleteOrderItemsTests {

    @Test
    @DisplayName("deleteOrderItems clears all items from order")
    void shouldClearAllItemsFromOrder() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));
      when(OrderRepositoryImplTest.this.orderJpaRepository.save(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.orderEntity);

      // WHEN
      OrderRepositoryImplTest.this.orderRepository.deleteOrderItems(
          OrderRepositoryImplTest.this.orderId);

      // THEN
      assertThat(OrderRepositoryImplTest.this.orderEntity.getItems()).isEmpty();
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .save(OrderRepositoryImplTest.this.orderEntity);
      verifyNoMoreInteractions(OrderRepositoryImplTest.this.orderJpaRepository);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
    }

    @Test
    @DisplayName("deleteOrderItems throws OrderNotFoundException when order not found")
    void shouldThrowOrderNotFoundExceptionWhenOrderNotFound() {
      // GIVEN
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.empty());

      // WHEN & THEN
      assertThatThrownBy(
              () ->
                  OrderRepositoryImplTest.this.orderRepository.deleteOrderItems(
                      OrderRepositoryImplTest.this.orderId))
          .isInstanceOf(OrderNotFoundException.class)
          .hasMessageContaining(OrderRepositoryImplTest.this.orderId.toString());

      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
    }
  }

  @Nested
  @DisplayName("Tests for purchaseOrder")
  class PurchaseOrderTests {

    @Test
    @DisplayName("purchaseOrder updates and returns purchased order")
    void shouldUpdateAndReturnPurchasedOrder() {
      // GIVEN
      final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
      final String shippingAddress = Instancio.create(String.class);
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.of(OrderRepositoryImplTest.this.orderEntity));
      doNothing()
          .when(OrderRepositoryImplTest.this.orderEntityHandler)
          .updateOrderPaymentAndShipping(
              OrderRepositoryImplTest.this.orderEntity, paymentMethod, shippingAddress);
      doNothing()
          .when(OrderRepositoryImplTest.this.orderEntityHandler)
          .syncOrderItemsWithDomain(
              OrderRepositoryImplTest.this.orderEntity, OrderRepositoryImplTest.this.order);
      when(OrderRepositoryImplTest.this.orderJpaRepository.save(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.orderEntity);
      when(OrderRepositoryImplTest.this.orderEntityMapper.toDomain(
              OrderRepositoryImplTest.this.orderEntity))
          .thenReturn(OrderRepositoryImplTest.this.order);

      // WHEN
      final Order result =
          OrderRepositoryImplTest.this.orderRepository.purchaseOrder(
              OrderRepositoryImplTest.this.order, paymentMethod, shippingAddress);

      // THEN
      assertThat(result).isEqualTo(OrderRepositoryImplTest.this.order);
      assertThat(OrderRepositoryImplTest.this.orderEntity.getStatus())
          .isEqualTo(OrderStatus.PURCHASED);
      assertThat(OrderRepositoryImplTest.this.orderEntity.getTotalPrice())
          .isEqualTo(OrderRepositoryImplTest.this.order.getTotalPrice());
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verify(OrderRepositoryImplTest.this.orderEntityHandler)
          .updateOrderPaymentAndShipping(
              OrderRepositoryImplTest.this.orderEntity, paymentMethod, shippingAddress);
      verify(OrderRepositoryImplTest.this.orderEntityHandler)
          .syncOrderItemsWithDomain(
              OrderRepositoryImplTest.this.orderEntity, OrderRepositoryImplTest.this.order);
      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .save(OrderRepositoryImplTest.this.orderEntity);
      verify(OrderRepositoryImplTest.this.orderEntityMapper)
          .toDomain(OrderRepositoryImplTest.this.orderEntity);
      verifyNoMoreInteractions(
          OrderRepositoryImplTest.this.orderJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("purchaseOrder throws OrderNotFoundException when order not found")
    void shouldThrowOrderNotFoundExceptionWhenOrderNotFound() {
      // GIVEN
      final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
      final String shippingAddress = Instancio.create(String.class);
      when(OrderRepositoryImplTest.this.orderJpaRepository.findById(
              OrderRepositoryImplTest.this.orderId))
          .thenReturn(Optional.empty());

      // WHEN & THEN
      assertThatThrownBy(
              () ->
                  OrderRepositoryImplTest.this.orderRepository.purchaseOrder(
                      OrderRepositoryImplTest.this.order, paymentMethod, shippingAddress))
          .isInstanceOf(OrderNotFoundException.class)
          .hasMessageContaining(OrderRepositoryImplTest.this.orderId.toString());

      verify(OrderRepositoryImplTest.this.orderJpaRepository)
          .findById(OrderRepositoryImplTest.this.orderId);
      verifyNoInteractions(
          OrderRepositoryImplTest.this.customerJpaRepository,
          OrderRepositoryImplTest.this.bookJpaRepository,
          OrderRepositoryImplTest.this.orderEntityMapper);
    }
  }
}
