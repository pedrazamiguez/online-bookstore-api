package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.OrderJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.OrderEntityMapper;
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

    final OrderEntity orderEntityToSave = this.orderEntityMapper.toNewOrderEntity(customerEntity);
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

    this.patchWithItem(existingOrderEntity, bookId, quantity);
    final OrderEntity savedOrderEntity = this.orderJpaRepository.save(existingOrderEntity);
    return this.orderEntityMapper.toDomain(savedOrderEntity);
  }

  private void patchWithItem(
      final OrderEntity existingOrderEntity, final Long bookId, final Long quantity) {
    if (this.isBookAlreadyInOrder(existingOrderEntity, bookId)) {
      this.orderEntityMapper.patchWithExistingOrderItem(existingOrderEntity, bookId, quantity);
    } else {
      final BookEntity bookEntity =
          this.bookJpaRepository
              .findById(bookId)
              .orElseThrow(() -> new BookNotFoundException(bookId));
      this.orderEntityMapper.patchWithNewOrderItem(existingOrderEntity, bookEntity, quantity);
    }
  }

  private boolean isBookAlreadyInOrder(final OrderEntity orderEntity, final Long bookId) {
    return orderEntity.getItems().stream()
        .anyMatch(orderItem -> bookId.equals(orderItem.getBook().getId()));
  }
}
