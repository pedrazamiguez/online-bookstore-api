package es.pedrazamiguez.onlinebookstore.domain.service.payment;

import es.pedrazamiguez.onlinebookstore.domain.enums.PaymentMethod;
import java.math.BigDecimal;

public interface PaymentService {

  void processPayment(BigDecimal amount, PaymentMethod paymentMethod, Long orderId);
}
