package es.pedrazamiguez.api.onlinebookstore.repository.jpa;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

  Optional<OrderEntity> findOneByCustomer_usernameAndStatusOrderByUpdatedAtDesc(
      String username, OrderStatus status);
}
