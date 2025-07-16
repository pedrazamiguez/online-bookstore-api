package es.pedrazamiguez.api.onlinebookstore.repository.impl;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.api.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotInOrderException;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderItemEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.handler.OrderEntityHandler;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.OrderJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.mapper.OrderEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;

  private final CustomerJpaRepository customerJpaRepository;

  private final BookJpaRepository bookJpaRepository;

  private final OrderEntityMapper orderEntityMapper;

  private final OrderEntityHandler orderEntityHandler;

  @Override
  public Optional<Order> findCreatedOrderForCustomer(final String username) {
    log.info("Finding created order for customer: {}", username);
    return this.orderJpaRepository
        .findOneByCustomer_usernameAndStatusOrderByUpdatedAtDesc(username, OrderStatus.CREATED)
        .map(this.orderEntityMapper::toDomain);
  }

  @Override
  public Order createNewOrder(final String username) {
    log.info("Creating new order for customer: {}", username);
    final CustomerEntity customerEntity =
        this.customerJpaRepository
            .findByUsername(username)
            .orElseThrow(() -> new CustomerNotFoundException(username));

    final OrderEntity orderEntityToSave = this.orderEntityHandler.createNewOrder(customerEntity);
    final OrderEntity savedOrderEntity = this.orderJpaRepository.save(orderEntityToSave);
    return this.orderEntityMapper.toDomain(savedOrderEntity);
  }

  @Override
  public Order saveOrderItem(final Long orderId, final Long bookId, final Long quantity) {
    log.info(
        "Saving order item for orderId: {}, bookId: {}, quantity: {}", orderId, bookId, quantity);

    final OrderEntity existingOrderEntity =
        this.orderJpaRepository
            .findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

    this.patchWithAddedItem(existingOrderEntity, bookId, quantity);
    final OrderEntity savedOrderEntity = this.orderJpaRepository.save(existingOrderEntity);
    return this.orderEntityMapper.toDomain(savedOrderEntity);
  }

  @Override
  public Order deleteOrderItem(final Long orderId, final Long bookId, final Long quantity) {
    log.info(
        "Deleting order item for orderId: {}, bookId: {}, quantity: {}", orderId, bookId, quantity);

    final OrderEntity existingOrderEntity =
        this.orderJpaRepository
            .findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

    this.patchWithDeletedItem(existingOrderEntity, bookId, quantity);
    final OrderEntity savedOrderEntity = this.orderJpaRepository.save(existingOrderEntity);
    return this.orderEntityMapper.toDomain(savedOrderEntity);
  }

  @Override
  public void deleteOrderItems(final Long orderId) {
    log.info("Deleting order items for orderId: {}", orderId);

    final OrderEntity existingOrderEntity =
        this.orderJpaRepository
            .findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

    existingOrderEntity.getItems().clear();
    this.orderJpaRepository.save(existingOrderEntity);
  }

  @Override
  public Order purchaseOrder(
      final Order order, final PaymentMethod paymentMethod, final String shippingAddress) {

    final Long orderId = order.getId();
    log.info("Purchasing order for: {}", order);

    final OrderEntity existingOrderEntity =
        this.orderJpaRepository
            .findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

    this.orderEntityHandler.updateOrderPaymentAndShipping(
        existingOrderEntity, paymentMethod, shippingAddress);
    this.orderEntityHandler.syncOrderItemsWithDomain(existingOrderEntity, order);
    existingOrderEntity.setTotalPrice(order.getTotalPrice());
    existingOrderEntity.setStatus(OrderStatus.PURCHASED);

    final OrderEntity savedOrderEntity = this.orderJpaRepository.save(existingOrderEntity);
    return this.orderEntityMapper.toDomain(savedOrderEntity);
  }

  private void patchWithAddedItem(
      final OrderEntity existingOrderEntity, final Long bookId, final Long quantity) {

    final BookEntity bookEntity =
        existingOrderEntity.getItems().stream()
            .map(OrderItemEntity::getBook)
            .filter(book -> bookId.equals(book.getId()))
            .findFirst()
            .orElseGet(
                () ->
                    this.bookJpaRepository
                        .findById(bookId)
                        .orElseThrow(() -> new BookNotFoundException(bookId)));

    this.orderEntityHandler.addToOrder(existingOrderEntity, bookEntity, quantity);
  }

  private void patchWithDeletedItem(
      final OrderEntity existingOrderEntity, final Long bookId, final Long quantity) {

    final Optional<OrderItemEntity> existingItemOpt =
        existingOrderEntity.getItems().stream()
            .filter(item -> bookId.equals(item.getBook().getId()))
            .findFirst();

    if (existingItemOpt.isPresent()) {
      this.orderEntityHandler.removeFromOrder(existingOrderEntity, bookId, quantity);
    } else {
      throw new BookNotInOrderException(bookId, existingOrderEntity.getId());
    }
  }
}
