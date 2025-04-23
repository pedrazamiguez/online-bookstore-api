package es.pedrazamiguez.assessment.onlinebookstore.application.adapter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.*;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateSubtotalAdapterImplTest {

  private CalculateSubtotalAdapterImpl calculateSubtotalAdapter;

  @Mock private SubtotalPriceService newReleaseSubtotalPriceService;
  @Mock private SubtotalPriceService regularSubtotalPriceService;
  @Mock private SubtotalPriceService oldEditionSubtotalPriceService;
  @Mock private OrderItem orderItem;
  @Mock private BookAllocation allocation;
  @Mock private Book book;
  @Mock private BookType bookType;
  @Mock private PayableAmount payableAmount;

  @BeforeEach
  void setUp() {
    // Mock book type codes for strategies
    when(this.newReleaseSubtotalPriceService.getBookTypeCode()).thenReturn("NEW_RELEASE");
    when(this.regularSubtotalPriceService.getBookTypeCode()).thenReturn("REGULAR");
    when(this.oldEditionSubtotalPriceService.getBookTypeCode()).thenReturn("OLD_EDITION");

    // Initialize adapter with mocked strategies
    this.calculateSubtotalAdapter =
        new CalculateSubtotalAdapterImpl(
            List.of(
                this.newReleaseSubtotalPriceService,
                this.regularSubtotalPriceService,
                this.oldEditionSubtotalPriceService));
  }

  @Test
  void givenEmptyStrategiesList_whenCalculateSubtotal_thenThrowsIllegalArgumentException() {
    // GIVEN
    this.calculateSubtotalAdapter = new CalculateSubtotalAdapterImpl(Collections.emptyList());
    final OrderItem givenOrderItem = Instancio.create(OrderItem.class);

    // WHEN
    assertThatThrownBy(() -> this.calculateSubtotalAdapter.calculateSubtotal(givenOrderItem))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No price service found for book type");
  }

  @Test
  void givenNewReleaseBookType_whenCalculateSubtotal_thenReturnsCorrectPayableAmount() {
    // GIVEN
    when(this.orderItem.getAllocation()).thenReturn(this.allocation);
    when(this.allocation.getBook()).thenReturn(this.book);
    when(this.book.getType()).thenReturn(this.bookType);
    when(this.bookType.getCode()).thenReturn("NEW_RELEASE");
    when(this.newReleaseSubtotalPriceService.calculateSubtotal(this.orderItem))
        .thenReturn(this.payableAmount);

    // WHEN
    final PayableAmount result = this.calculateSubtotalAdapter.calculateSubtotal(this.orderItem);

    // THEN
    assertThat(result).isEqualTo(this.payableAmount);
    verify(this.newReleaseSubtotalPriceService).calculateSubtotal(this.orderItem);
    verify(this.regularSubtotalPriceService, never()).calculateSubtotal(any());
    verify(this.oldEditionSubtotalPriceService, never()).calculateSubtotal(any());
  }

  @Test
  void givenNullOrderItem_whenCalculateSubtotal_thenThrowsIllegalArgumentException() {
    // WHEN
    assertThatThrownBy(() -> this.calculateSubtotalAdapter.calculateSubtotal(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Order item or its allocation/book cannot be null");
  }

  @Test
  void givenNullAllocation_whenCalculateSubtotal_thenThrowsIllegalArgumentException() {
    // GIVEN
    when(this.orderItem.getAllocation()).thenReturn(null);

    // WHEN
    assertThatThrownBy(() -> this.calculateSubtotalAdapter.calculateSubtotal(this.orderItem))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Order item or its allocation/book cannot be null");
  }

  @Test
  void givenNullBook_whenCalculateSubtotal_thenThrowsIllegalArgumentException() {
    // GIVEN
    when(this.orderItem.getAllocation()).thenReturn(this.allocation);
    when(this.allocation.getBook()).thenReturn(null);

    // WHEN
    assertThatThrownBy(() -> this.calculateSubtotalAdapter.calculateSubtotal(this.orderItem))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Order item or its allocation/book cannot be null");
  }

  @Test
  void givenUnknownBookType_whenCalculateSubtotal_thenThrowsIllegalArgumentException() {
    // GIVEN
    when(this.orderItem.getAllocation()).thenReturn(this.allocation);
    when(this.allocation.getBook()).thenReturn(this.book);
    when(this.book.getType()).thenReturn(this.bookType);
    when(this.bookType.getCode()).thenReturn("UNKNOWN_TYPE");

    // WHEN
    assertThatThrownBy(() -> this.calculateSubtotalAdapter.calculateSubtotal(this.orderItem))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No price service found for book type: %s", this.bookType);
  }

  @Test
  void givenNullBookType_whenCalculateSubtotal_thenThrowsIllegalArgumentException() {
    // GIVEN
    when(this.orderItem.getAllocation()).thenReturn(this.allocation);
    when(this.allocation.getBook()).thenReturn(this.book);
    when(this.book.getType()).thenReturn(null);

    // WHEN
    assertThatThrownBy(() -> this.calculateSubtotalAdapter.calculateSubtotal(this.orderItem))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("No price service found for book type: null");
  }

  @ParameterizedTest
  @ValueSource(strings = {"NEW_RELEASE", "REGULAR", "OLD_EDITION"})
  void givenValidBookType_whenCalculateSubtotal_thenReturnsCorrectPayableAmount(
      final String bookTypeCode) {

    // GIVEN
    final SubtotalPriceService strategy = mock(SubtotalPriceService.class);
    when(strategy.getBookTypeCode()).thenReturn(bookTypeCode);
    this.calculateSubtotalAdapter = new CalculateSubtotalAdapterImpl(List.of(strategy));
    when(this.orderItem.getAllocation()).thenReturn(this.allocation);
    when(this.allocation.getBook()).thenReturn(this.book);
    when(this.book.getType()).thenReturn(this.bookType);
    when(this.bookType.getCode()).thenReturn(bookTypeCode);
    when(strategy.calculateSubtotal(this.orderItem)).thenReturn(this.payableAmount);

    // WHEN
    final PayableAmount result = this.calculateSubtotalAdapter.calculateSubtotal(this.orderItem);

    // THEN
    assertThat(result).isEqualTo(this.payableAmount);
    verify(strategy).calculateSubtotal(this.orderItem);
  }

  @Test
  void givenNullStrategiesList_whenConstructAdapter_thenThrowsIllegalArgumentException() {
    // WHEN
    assertThatThrownBy(() -> new CalculateSubtotalAdapterImpl(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Strategies list cannot be null");
  }

  @Test
  void
      givenStrategiesListWithNullElement_whenConstructAdapter_thenThrowsIllegalArgumentException() {

    // GIVEN
    final List<SubtotalPriceService> strategies =
        Arrays.asList(this.newReleaseSubtotalPriceService, null);

    // WHEN
    assertThatThrownBy(() -> new CalculateSubtotalAdapterImpl(strategies))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Strategies list cannot contain null elements");
  }

  @Test
  void givenDuplicateBookTypeCodes_whenConstructAdapter_thenThrowsIllegalArgumentException() {
    // GIVEN
    final SubtotalPriceService strategy1 = mock(SubtotalPriceService.class);
    final SubtotalPriceService strategy2 = mock(SubtotalPriceService.class);
    when(strategy1.getBookTypeCode()).thenReturn("NEW_RELEASE");
    when(strategy2.getBookTypeCode()).thenReturn("NEW_RELEASE");
    final List<SubtotalPriceService> strategies = List.of(strategy1, strategy2);

    // WHEN
    assertThatThrownBy(() -> new CalculateSubtotalAdapterImpl(strategies))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Duplicate book type code");
  }
}
