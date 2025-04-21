package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import lombok.Data;

@Data
public class OrderItem {
  private Long orderId;
  private BookAllocation allocation;
  private PayableAmount payableAmount;
}
