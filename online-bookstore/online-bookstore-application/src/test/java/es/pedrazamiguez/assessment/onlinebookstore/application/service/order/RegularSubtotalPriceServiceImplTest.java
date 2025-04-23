package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RegularSubtotalPriceServiceImplTest {

  private RegularSubtotalPriceServiceImpl service;

  @BeforeEach
  void setUp() {
    this.service = new RegularSubtotalPriceServiceImpl();
  }

  @Test
  @DisplayName("Correct book type code returned")
  void givenRegularService_whenGettingBookTypeCode_thenRegularCodeReturned() {
    // GIVEN
    // Service is initialized

    // WHEN
    final String bookTypeCode = this.service.getBookTypeCode();

    // THEN
    assertThat(bookTypeCode).isEqualTo("REGULAR");
  }

  @Test
  @DisplayName("No discount applied when copies below minimum")
  void givenFewerThanMinimumCopies_whenCalculatingSubtotal_thenNoDiscountApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 2L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(BigDecimal.ONE);
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("40.00")); // 20 * 2 * 1
  }

  @Test
  @DisplayName("Additional discount applied when copies meet minimum")
  void givenMinimumCopiesMet_whenCalculatingSubtotal_thenAdditionalDiscountApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 3L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(new BigDecimal("0.9"));
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("54.00")); // 20 * 3 * 0.9
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
