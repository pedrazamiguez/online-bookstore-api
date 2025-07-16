package es.pedrazamiguez.onlinebookstore.domain.model;

import es.pedrazamiguez.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.onlinebookstore.domain.enums.PaymentMethod;
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
