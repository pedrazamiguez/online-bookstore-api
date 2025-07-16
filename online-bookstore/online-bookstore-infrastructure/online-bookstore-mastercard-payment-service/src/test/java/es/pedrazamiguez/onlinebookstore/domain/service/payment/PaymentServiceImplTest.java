package es.pedrazamiguez.onlinebookstore.domain.service.payment;

import static org.assertj.core.api.Assertions.assertThat;

import es.pedrazamiguez.onlinebookstore.domain.enums.PaymentMethod;
import java.math.BigDecimal;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentServiceImplTest {

  private LogCaptor logCaptor;
  private PaymentServiceImpl paymentService;

  @BeforeEach
  void setUp() {
    // Initialize LogCaptor for the class under test
    this.logCaptor = LogCaptor.forClass(PaymentServiceImpl.class);
    this.paymentService = new PaymentServiceImpl();
  }

  @AfterEach
  void tearDown() {
    // Clear LogCaptor to avoid interference between tests
    this.logCaptor.close();
  }

  @Test
  void testProcessPayment_logsCorrectMessage() {
    // GIVEN
    final BigDecimal amount = new BigDecimal("98.23");
    final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    final Long orderId = 1L;
    final String expectedLogMessage =
        "Processing payment of 98.23 for orderId 1 using payment method CREDIT_CARD";

    // WHEN
    this.paymentService.processPayment(amount, paymentMethod, orderId);

    // THEN
    assertThat(this.logCaptor.getInfoLogs()).hasSize(1).containsExactly(expectedLogMessage);
  }
}
