package es.pedrazamiguez.api.onlinebookstore.domain.service.shipping;

import static org.assertj.core.api.Assertions.assertThat;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShippingServiceImplTest {

  private LogCaptor logCaptor;
  private ShippingServiceImpl shippingService;

  @BeforeEach
  void setUp() {
    // Initialize LogCaptor for the class under test
    this.logCaptor = LogCaptor.forClass(ShippingServiceImpl.class);
    this.shippingService = new ShippingServiceImpl();
  }

  @AfterEach
  void tearDown() {
    // Clear LogCaptor to avoid interference between tests
    this.logCaptor.close();
  }

  @Test
  void testProcessShipping_logsCorrectMessage() {
    // GIVEN
    final String shippingAddress = "123 Main St";
    final Long orderId = 1L;
    final String expectedLogMessage = "Processing shipping for orderId 1 to address 123 Main St";

    // WHEN
    this.shippingService.processShipping(shippingAddress, orderId);

    // THEN
    assertThat(this.logCaptor.getInfoLogs()).hasSize(1).containsExactly(expectedLogMessage);
  }
}
