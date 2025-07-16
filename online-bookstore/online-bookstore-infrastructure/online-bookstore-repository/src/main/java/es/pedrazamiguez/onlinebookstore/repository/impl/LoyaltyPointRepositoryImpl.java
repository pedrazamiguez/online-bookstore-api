package es.pedrazamiguez.onlinebookstore.repository.impl;

import es.pedrazamiguez.onlinebookstore.domain.enums.LoyaltyPointStatus;
import es.pedrazamiguez.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.onlinebookstore.domain.repository.LoyaltyPointRepository;
import es.pedrazamiguez.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.onlinebookstore.repository.entity.LoyaltyPointEntity;
import es.pedrazamiguez.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.onlinebookstore.repository.jpa.LoyaltyPointJpaRepository;
import es.pedrazamiguez.onlinebookstore.repository.jpa.OrderJpaRepository;
import java.util.List;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LoyaltyPointRepositoryImpl implements LoyaltyPointRepository {

  private final CustomerJpaRepository customerJpaRepository;

  private final OrderJpaRepository orderJpaRepository;

  private final LoyaltyPointJpaRepository loyaltyPointJpaRepository;

  @Override
  public void addLoyaltyPoints(final String username, final Long orderId, final Long points) {
    log.info("Adding loyalty points for user: {} with points: {}", username, points);

    final CustomerEntity customerEntity =
        this.customerJpaRepository
            .findByUsername(username)
            .orElseThrow(() -> new CustomerNotFoundException(username));

    final OrderEntity orderEntity =
        this.orderJpaRepository
            .findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

    final List<LoyaltyPointEntity> loyaltyPointsToSave =
        this.toEarnedLoyaltyPoints(customerEntity, orderEntity, points);
    this.loyaltyPointJpaRepository.saveAll(loyaltyPointsToSave);
  }

  @Override
  public void redeemLoyaltyPoints(final String username, final Long orderId, final Long points) {
    throw new UnsupportedOperationException(
        "Redeeming loyalty points is not supported in this implementation.");
  }

  @Override
  public Long getLoyaltyPoints(final String username) {
    log.info("Getting earned loyalty points for user: {}", username);
    return this.loyaltyPointJpaRepository.countLoyaltyPointsByCustomerUsernameAndStatusIn(
        username, LoyaltyPointStatus.EARNED.name());
  }

  private List<LoyaltyPointEntity> toEarnedLoyaltyPoints(
      final CustomerEntity customerEntity, final OrderEntity orderEntity, final Long points) {

    return LongStream.range(0, points)
        .mapToObj(
            i -> {
              final LoyaltyPointEntity loyaltyPointEntity = new LoyaltyPointEntity();
              loyaltyPointEntity.setCustomer(customerEntity);
              loyaltyPointEntity.setOrder(orderEntity);
              loyaltyPointEntity.setStatus(LoyaltyPointStatus.EARNED);
              return loyaltyPointEntity;
            })
        .toList();
  }
}
