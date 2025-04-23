package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PayableAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OldEditionSubtotalPriceServiceImplTest {

  private OldEditionSubtotalPriceServiceImpl service;

  @BeforeEach
  void setUp() {
    this.service = new OldEditionSubtotalPriceServiceImpl();
  }

  @Test
  @DisplayName("Correct book type code returned")
  void givenOldEditionService_whenGettingBookTypeCode_thenOldEditionCodeReturned() {
    // WHEN
    final String bookTypeCode = this.service.getBookTypeCode();

    // THEN
    assertThat(bookTypeCode).isEqualTo("OLD_EDITION");
  }

  @Test
  @DisplayName("Default discount applied when copies below minimum")
  void givenFewerThanMinimumCopies_whenCalculatingSubtotal_thenDefaultDiscountApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 2L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(new BigDecimal("0.8"));
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("32.00")); // 20 * 2 * 0.8
  }

  @Test
  @DisplayName("Both discounts applied when copies meet minimum")
  void givenMinimumCopiesMet_whenCalculatingSubtotal_thenBothDiscountsApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 3L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(new BigDecimal("0.76")); // 0.8 * 0.95
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("45.60")); // 20 * 3 * 0.76
  }

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
}
