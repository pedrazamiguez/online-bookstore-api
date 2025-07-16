package es.pedrazamiguez.api.onlinebookstore.repository.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table(
    name = "order_items",
    indexes = {
      @Index(name = "idx_order_items_order_id", columnList = "order_id"),
      @Index(name = "idx_order_items_book_id", columnList = "book_id"),
      @Index(name = "idx_order_items_quantity", columnList = "quantity"),
      @Index(name = "idx_order_items_book_id_quantity", columnList = "book_id, quantity")
    })
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
