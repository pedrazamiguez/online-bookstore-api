package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.LoyaltyPointStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.LoyaltyPointRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.LoyaltyPointEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.LoyaltyPointJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.OrderJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.LoyaltyPointMapper;
import java.util.List;
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

    private final LoyaltyPointMapper loyaltyPointMapper;

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
                this.loyaltyPointMapper.toEarnedLoyaltyPoints(customerEntity, orderEntity, points);
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
}
