package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItem {
  private Long orderId;
  private BookAllocation allocation;
  private BigDecimal discount;
  private BigDecimal subtotal;
}
