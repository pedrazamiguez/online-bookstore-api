package es.pedrazamiguez.api.onlinebookstore.application.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.api.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.api.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.api.onlinebookstore.domain.model.PayableAmount;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegularSubtotalPriceServiceImplTest {

  private RegularSubtotalPriceServiceImpl service;

  @Mock private DiscountConfigurationProperties discountConfigurationProperties;

  @Mock private DiscountConfigurationProperties.Regular regular;

  @BeforeEach
  void setUp() {
    this.service = new RegularSubtotalPriceServiceImpl(this.discountConfigurationProperties);
  }

  @Test
  @DisplayName("Correct book type code returned")
  void givenRegularService_whenGettingBookTypeCode_thenRegularCodeReturned() {
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
    when(this.discountConfigurationProperties.getDefaultMinimumCopies()).thenReturn(3L);

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
    when(this.discountConfigurationProperties.getRegular()).thenReturn(this.regular);
    when(this.regular.getBundle()).thenReturn(new BigDecimal("0.9"));

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
