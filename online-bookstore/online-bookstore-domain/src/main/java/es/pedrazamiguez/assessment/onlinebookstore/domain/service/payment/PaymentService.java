package es.pedrazamiguez.assessment.onlinebookstore.domain.service.payment;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import java.math.BigDecimal;

public interface PaymentService {

    void processPayment(BigDecimal amount, PaymentMethod paymentMethod, Long orderId);
}
