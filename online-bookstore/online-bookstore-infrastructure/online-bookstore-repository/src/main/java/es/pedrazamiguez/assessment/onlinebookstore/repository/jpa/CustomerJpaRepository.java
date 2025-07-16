package es.pedrazamiguez.api.onlinebookstore.repository.jpa;

import es.pedrazamiguez.api.onlinebookstore.repository.entity.CustomerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

  Optional<CustomerEntity> findByUsername(String username);
}
