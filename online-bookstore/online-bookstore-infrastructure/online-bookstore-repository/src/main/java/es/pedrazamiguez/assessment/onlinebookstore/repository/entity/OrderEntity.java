package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table(
    name = "orders",
    indexes = {
      @Index(name = "idx_orders_customer_id", columnList = "customer_id"),
      @Index(name = "idx_orders_status", columnList = "status"),
    })
public class OrderEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "customer_id")
  private CustomerEntity customer;

  @Column(nullable = false, precision = 10, scale = 4)
  private BigDecimal totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItemEntity> items = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column
  private PaymentMethod paymentMethod;

  @Column private String shippingAddress;
}
