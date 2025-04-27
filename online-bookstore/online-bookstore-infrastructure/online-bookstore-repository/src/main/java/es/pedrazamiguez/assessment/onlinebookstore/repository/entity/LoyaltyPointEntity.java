package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.LoyaltyPointStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table(
        name = "loyalty_points",
        indexes = {
            @Index(name = "idx_loyalty_points_customer_id", columnList = "customer_id"),
            @Index(name = "idx_loyalty_points_order_id", columnList = "order_id"),
            @Index(name = "idx_loyalty_points_status", columnList = "status"),
        })
public class LoyaltyPointEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoyaltyPointStatus status;
}
