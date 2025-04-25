package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PayableAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NewReleaseSubtotalPriceServiceImplTest {

  private NewReleaseSubtotalPriceServiceImpl service;

  @Mock private DiscountConfigurationProperties discountConfigurationProperties;

  @BeforeEach
  void setUp() {
    this.service = new NewReleaseSubtotalPriceServiceImpl(this.discountConfigurationProperties);
  }

  @Test
  @DisplayName("Correct book type code returned")
  void givenNewReleaseService_whenGettingBookTypeCode_thenNewReleaseCodeReturned() {
    // WHEN
    final String bookTypeCode = this.service.getBookTypeCode();

    // THEN
    assertThat(bookTypeCode).isEqualTo("NEW_RELEASE");
  }

  @Test
  @DisplayName("No discount applied regardless of copies")
  void givenAnyNumberOfCopies_whenCalculatingSubtotal_thenNoDiscountApplied() {
    // GIVEN
    final OrderItem orderItem = this.createOrderItem(new BigDecimal("20.00"), 5L);

    // WHEN
    final PayableAmount result = this.service.calculateSubtotal(orderItem);

    // THEN
    assertThat(result.getDiscount()).isEqualByComparingTo(BigDecimal.ONE);
    assertThat(result.getSubtotal()).isEqualByComparingTo(new BigDecimal("100.00")); // 20 * 5 * 1
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
