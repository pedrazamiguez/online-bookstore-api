package es.pedrazamiguez.onlinebookstore.application.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import es.pedrazamiguez.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.onlinebookstore.domain.model.PayableAmount;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OldEditionSubtotalPriceServiceImplTest {

  private OldEditionSubtotalPriceServiceImpl service;

  @Mock private DiscountConfigurationProperties discountConfigurationProperties;

  @Mock private DiscountConfigurationProperties.OldEdition oldEdition;

  @BeforeEach
  void setUp() {
    this.service = new OldEditionSubtotalPriceServiceImpl(this.discountConfigurationProperties);
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
    when(this.discountConfigurationProperties.getOldEdition()).thenReturn(this.oldEdition);
    when(this.oldEdition.getDefaultDiscount()).thenReturn(new BigDecimal("0.8"));
    when(this.discountConfigurationProperties.getDefaultMinimumCopies()).thenReturn(3L);

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
    when(this.discountConfigurationProperties.getOldEdition()).thenReturn(this.oldEdition);
    when(this.oldEdition.getDefaultDiscount()).thenReturn(new BigDecimal("0.8"));
    when(this.oldEdition.getBundle()).thenReturn(new BigDecimal("0.95"));
    when(this.discountConfigurationProperties.getDefaultMinimumCopies()).thenReturn(3L);

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
