package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
// @NoArgsConstructor
// @AllArgsConstructor
public class OrderItemId
// implements Serializable
{

  @Column(name = "order_id", nullable = false)
  private Long orderId;

  @Column(name = "line_number", nullable = false)
  private Integer lineNumber;
}
