package es.pedrazamiguez.assessment.onlinebookstore.repository.jpa;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.OrderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findOneByCustomer_usernameAndStatusOrderByUpdatedAtDesc(
            String username, OrderStatus status);
}
