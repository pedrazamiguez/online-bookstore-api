package es.pedrazamiguez.api.onlinebookstore.domain.model;

import lombok.Data;

@Data
public class OrderItem {
  private Long orderId;
  private BookAllocation allocation;
  private PayableAmount payableAmount;
}
