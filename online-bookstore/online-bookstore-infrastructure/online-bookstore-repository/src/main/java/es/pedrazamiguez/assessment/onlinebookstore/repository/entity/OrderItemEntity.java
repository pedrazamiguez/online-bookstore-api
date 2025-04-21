package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table(name = "order_items")
public class OrderItemEntity extends AuditEntity {

  @EmbeddedId private OrderItemId id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id", nullable = false, insertable = false, updatable = false)
  @MapsId("orderId")
  private OrderEntity order;

  @ManyToOne(optional = false)
  @JoinColumn(name = "book_id")
  private BookEntity book;

  @Column(nullable = false)
  private Long quantity;

  @Column(precision = 10, scale = 4)
  private BigDecimal purchasedUnitPrice;

  @Column(precision = 10, scale = 4)
  private BigDecimal purchasedDiscountRate;
}
