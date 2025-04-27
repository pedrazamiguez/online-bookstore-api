package es.pedrazamiguez.assessment.onlinebookstore.domain.service.payment;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
  @Override
  public void processPayment(
      final BigDecimal amount, final PaymentMethod paymentMethod, final Long orderId) {

    log.info(
        "Processing payment of {} for orderId {} using payment method {}",
        amount,
        orderId,
        paymentMethod);
  }
}
