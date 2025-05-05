package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.adapter.CalculateSubtotalAdapter;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PayableAmount;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.altindag.log.LogCaptor;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinalPriceServiceImplTest {

  @InjectMocks private FinalPriceServiceImpl finalPriceService;

  @Mock private CalculateSubtotalAdapter calculateSubtotalAdapter;

  private LogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    this.logCaptor = LogCaptor.forClass(FinalPriceServiceImpl.class);
  }

  @AfterEach
  void tearDown() {
    // Clear LogCaptor to avoid interference between tests
    this.logCaptor.close();
  }

  @Test
  @DisplayName("Calculate sets total price correctly for order with multiple lines")
  void shouldCalculateTotalPrice_whenOrderHasMultipleLines() {
    // GIVEN
    final Order order =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final OrderItem line1 = Instancio.create(OrderItem.class);
    final OrderItem line2 = Instancio.create(OrderItem.class);
    order.setLines(Arrays.asList(line1, line2));

    final PayableAmount amount1 = new PayableAmount();
    amount1.setSubtotal(new BigDecimal("10.00"));
    final PayableAmount amount2 = new PayableAmount();
    amount2.setSubtotal(new BigDecimal("20.00"));

    when(this.calculateSubtotalAdapter.calculateSubtotal(line1)).thenReturn(amount1);
    when(this.calculateSubtotalAdapter.calculateSubtotal(line2)).thenReturn(amount2);

    // WHEN
    this.finalPriceService.calculate(order);

    // THEN
    assertThat(order.getTotalPrice()).isEqualTo(new BigDecimal("30.00"));
    assertThat(line1.getPayableAmount()).isEqualTo(amount1);
    assertThat(line2.getPayableAmount()).isEqualTo(amount2);

    verify(this.calculateSubtotalAdapter).calculateSubtotal(line1);
    verify(this.calculateSubtotalAdapter).calculateSubtotal(line2);
    verifyNoMoreInteractions(this.calculateSubtotalAdapter);

    final List<String> infoLogs = this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(2);
    assertThat(infoLogs.get(0))
        .contains("Calculating final price for orderId", order.getId().toString());
    assertThat(infoLogs.get(1))
        .contains("Final price for orderId", order.getId().toString(), "30.00");
  }

  @Test
  @DisplayName("Calculate sets total price to zero for order with empty lines")
  void shouldSetTotalPriceToZero_whenOrderHasEmptyLines() {
    // GIVEN
    final Order order =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .set(field(Order::getLines), Collections.emptyList())
            .create();

    // WHEN
    this.finalPriceService.calculate(order);

    // THEN
    assertThat(order.getTotalPrice()).isEqualTo(BigDecimal.ZERO);

    verifyNoInteractions(this.calculateSubtotalAdapter);

    final List<String> infoLogs = this.logCaptor.getInfoLogs();
    final List<String> warnLogs = this.logCaptor.getWarnLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains("Calculating final price for orderId", order.getId().toString());
    assertThat(warnLogs.getFirst())
        .contains("has no lines to calculate final price", order.getId().toString());
  }

  @Test
  @DisplayName("Calculate sets total price correctly for order with single line")
  void shouldCalculateTotalPrice_whenOrderHasSingleLine() {
    // GIVEN
    final Order order =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final OrderItem line = Instancio.create(OrderItem.class);
    order.setLines(Collections.singletonList(line));

    final PayableAmount amount = new PayableAmount();
    amount.setSubtotal(new BigDecimal("15.50"));

    when(this.calculateSubtotalAdapter.calculateSubtotal(line)).thenReturn(amount);

    // WHEN
    this.finalPriceService.calculate(order);

    // THEN
    assertThat(order.getTotalPrice()).isEqualTo(new BigDecimal("15.50"));
    assertThat(line.getPayableAmount()).isEqualTo(amount);

    verify(this.calculateSubtotalAdapter).calculateSubtotal(line);
    verifyNoMoreInteractions(this.calculateSubtotalAdapter);

    final List<String> infoLogs = this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(2);
    assertThat(infoLogs.get(0))
        .contains("Calculating final price for orderId", order.getId().toString());
    assertThat(infoLogs.get(1))
        .contains("Final price for orderId", order.getId().toString(), "15.50");
  }

  @Test
  @DisplayName("Calculate sets total price to zero for order with zero subtotals")
  void shouldSetTotalPriceToZero_whenLinesHaveZeroSubtotals() {
    // GIVEN
    final Order order =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final OrderItem line1 = Instancio.create(OrderItem.class);
    final OrderItem line2 = Instancio.create(OrderItem.class);
    order.setLines(Arrays.asList(line1, line2));

    final PayableAmount amount1 = new PayableAmount();
    amount1.setSubtotal(BigDecimal.ZERO);
    final PayableAmount amount2 = new PayableAmount();
    amount2.setSubtotal(BigDecimal.ZERO);

    when(this.calculateSubtotalAdapter.calculateSubtotal(line1)).thenReturn(amount1);
    when(this.calculateSubtotalAdapter.calculateSubtotal(line2)).thenReturn(amount2);

    // WHEN
    this.finalPriceService.calculate(order);

    // THEN
    assertThat(order.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
    assertThat(line1.getPayableAmount()).isEqualTo(amount1);
    assertThat(line2.getPayableAmount()).isEqualTo(amount2);

    verify(this.calculateSubtotalAdapter).calculateSubtotal(line1);
    verify(this.calculateSubtotalAdapter).calculateSubtotal(line2);
    verifyNoMoreInteractions(this.calculateSubtotalAdapter);

    final List<String> infoLogs = this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(2);
    assertThat(infoLogs.get(0))
        .contains("Calculating final price for orderId", order.getId().toString());
    assertThat(infoLogs.get(1)).contains("Final price for orderId", order.getId().toString(), "0");
  }

  @Test
  @DisplayName("Calculate propagates exception when calculateSubtotalAdapter fails")
  void shouldPropagateException_whenCalculateSubtotalAdapterFails() {
    // GIVEN
    final Order order =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final OrderItem line = Instancio.create(OrderItem.class);
    order.setLines(Collections.singletonList(line));

    final RuntimeException exception = new RuntimeException("Subtotal calculation failed");
    when(this.calculateSubtotalAdapter.calculateSubtotal(line)).thenThrow(exception);

    // WHEN / THEN
    assertThatThrownBy(() -> this.finalPriceService.calculate(order))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Subtotal calculation failed");

    verify(this.calculateSubtotalAdapter).calculateSubtotal(line);
    verifyNoMoreInteractions(this.calculateSubtotalAdapter);

    final List<String> infoLogs = this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains("Calculating final price for orderId", order.getId().toString());
  }

  @Test
  @DisplayName("Calculate handles null lines by setting total price to zero")
  void shouldSetTotalPriceToZero_whenLinesAreNull() {
    // GIVEN
    final Order order =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .set(field(Order::getLines), null)
            .create();

    // WHEN
    this.finalPriceService.calculate(order);

    // THEN
    assertThat(order.getTotalPrice()).isEqualTo(BigDecimal.ZERO);

    verifyNoInteractions(this.calculateSubtotalAdapter);

    final List<String> infoLogs = this.logCaptor.getInfoLogs();
    final List<String> warnLogs = this.logCaptor.getWarnLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains("Calculating final price for orderId", order.getId().toString());
    assertThat(warnLogs.getFirst())
        .contains("has no lines to calculate final price", order.getId().toString());
  }
}
