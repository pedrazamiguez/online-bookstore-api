package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
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
}
