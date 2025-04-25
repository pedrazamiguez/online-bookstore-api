package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class OrderItemId {

  @Column(nullable = false)
  private Long orderId;

  @Column(nullable = false)
  private Integer lineNumber;
}
