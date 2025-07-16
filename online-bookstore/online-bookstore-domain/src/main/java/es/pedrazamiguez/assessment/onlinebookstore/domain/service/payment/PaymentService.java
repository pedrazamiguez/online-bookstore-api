package es.pedrazamiguez.api.onlinebookstore.domain.service.payment;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.PaymentMethod;
import java.math.BigDecimal;

public interface PaymentService {

  void processPayment(BigDecimal amount, PaymentMethod paymentMethod, Long orderId);
}
