package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class Order {
  private Long id;
  private List<OrderItem> lines;
  private OrderStatus status;
  private BigDecimal totalPrice;
  private PaymentMethod paymentMethod;
  private String shippingAddress;
}
