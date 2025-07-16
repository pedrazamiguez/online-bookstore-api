package es.pedrazamiguez.onlinebookstore.repository.jpa;

import es.pedrazamiguez.onlinebookstore.repository.entity.LoyaltyPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyPointJpaRepository extends JpaRepository<LoyaltyPointEntity, Long> {

  @Query(
      value =
          """
          select
            count(lp.id)
          from
            loyalty_points lp
            inner join customers c on lp.customer_id = c.id
          where
            c.username = :username
            and lp.status in (:statuses)
          """,
      nativeQuery = true)
  Long countLoyaltyPointsByCustomerUsernameAndStatusIn(String username, String... statuses);
}
