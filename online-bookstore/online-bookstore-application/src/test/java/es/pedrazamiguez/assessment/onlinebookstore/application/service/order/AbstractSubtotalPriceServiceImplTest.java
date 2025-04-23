package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import static org.assertj.core.api.Assertions.assertThat;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractSubtotalPriceServiceImplTest {

  private TestSubtotalPriceService service;

  @BeforeEach
  void setUp() {
    this.service = new TestSubtotalPriceService();
  }

  @Test
  @DisplayName("No discount applied when copies below minimum")
  void givenFewerThanMinimumCopies_whenCalculatingSubtotal_thenNoDiscountApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 2L);
    this.service.setDefaultDiscount(new BigDecimal("0.9"));
    this.service.setAdditionalDiscount(new BigDecimal("0.8"));
    this.service.setMinimumCopiesForDiscount(3L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(new BigDecimal("0.9"));
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("36.00")); // 20 * 2 * 0.9
  }

  @Test
  @DisplayName("Both discounts applied when copies meet minimum")
  void givenMinimumCopiesMet_whenCalculatingSubtotal_thenBothDiscountsApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 3L);
    this.service.setDefaultDiscount(new BigDecimal("0.9"));
    this.service.setAdditionalDiscount(new BigDecimal("0.8"));
    this.service.setMinimumCopiesForDiscount(3L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(new BigDecimal("0.72")); // 0.9 * 0.8
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("43.20")); // 20 * 3 * 0.72
  }

  @Test
  @DisplayName("No discount when default discount is 1")
  void givenDefaultDiscountOne_whenCalculatingSubtotal_thenNoDiscountApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 3L);
    this.service.setDefaultDiscount(BigDecimal.ONE);
    this.service.setAdditionalDiscount(BigDecimal.ONE);
    this.service.setMinimumCopiesForDiscount(3L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(new BigDecimal("1.00")); // 1 * 1
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("60.00")); // 20 * 3 * 1
  }

  // New test case to cover getAdditionalDiscount()
  @Test
  @DisplayName("Default additional discount applied when copies meet minimum")
  void givenMinimumCopiesMet_whenCalculatingSubtotal_thenDefaultAdditionalDiscountApplied() {
    // GIVEN
    final MinimalTestSubtotalPriceService minimalService = new MinimalTestSubtotalPriceService();
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 3L);

    // WHEN
    final PayableAmount result = minimalService.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(BigDecimal.ONE); // NO_DISCOUNT (1.0)
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("60.00")); // 20 * 3 * 1.0
  }

  // Helper method to create OrderItem
  private OrderItem createOrderItem(final BigDecimal bookPrice, final Long copies) {
    final Book book = new Book();
    book.setPrice(bookPrice);
    final BookAllocation allocation = new BookAllocation();
    allocation.setBook(book);
    allocation.setCopies(copies);
    final OrderItem orderItem = new OrderItem();
    orderItem.setAllocation(allocation);
    return orderItem;
  }

  // Test-specific subclass (existing)
  private static class TestSubtotalPriceService extends AbstractSubtotalPriceServiceImpl {
    private BigDecimal defaultDiscount = BigDecimal.ONE;
    private BigDecimal additionalDiscount = BigDecimal.ONE;
    private Long minimumCopiesForDiscount = 3L;

    @Override
    public String getBookTypeCode() {
      return "TEST";
    }

    @Override
    protected BigDecimal getDefaultDiscount() {
      return this.defaultDiscount;
    }

    void setDefaultDiscount(final BigDecimal defaultDiscount) {
      this.defaultDiscount = defaultDiscount;
    }

    @Override
    protected BigDecimal getAdditionalDiscount() {
      return this.additionalDiscount;
    }

    void setAdditionalDiscount(final BigDecimal additionalDiscount) {
      this.additionalDiscount = additionalDiscount;
    }

    @Override
    protected Long getMinimumCopiesForDiscount() {
      return this.minimumCopiesForDiscount;
    }

    void setMinimumCopiesForDiscount(final Long minimumCopiesForDiscount) {
      this.minimumCopiesForDiscount = minimumCopiesForDiscount;
    }
  }

  // New minimal test-specific subclass to cover default getAdditionalDiscount()
  private static class MinimalTestSubtotalPriceService extends AbstractSubtotalPriceServiceImpl {
    @Override
    public String getBookTypeCode() {
      return "MINIMAL_TEST";
    }
    // Do not override getAdditionalDiscount(), getDefaultDiscount(), or
    // getMinimumCopiesForDiscount()
    // Use their default implementations from AbstractSubtotalPriceServiceImpl
  }
}
