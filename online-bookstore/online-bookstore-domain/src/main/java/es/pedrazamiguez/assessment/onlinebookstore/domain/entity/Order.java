package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class Order {
  private Long id;
  private Customer customer;
  private List<BookAllocation> lines;
  private OrderStatus status;
  private BigDecimal totalPrice;
}
