package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PayableAmount;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import java.math.BigDecimal;

public abstract class AbstractSubtotalPriceServiceImpl implements SubtotalPriceService {

  private static final BigDecimal NO_DISCOUNT = BigDecimal.ONE;

  private static final Long DEFAULT_MINIMUM_COPIES_FOR_DISCOUNT = 3L;

  protected BigDecimal getDefaultDiscount() {
    return NO_DISCOUNT;
  }

  protected BigDecimal getAdditionalDiscount() {
    return NO_DISCOUNT;
  }

  protected Long getMinimumCopiesForDiscount() {
    return DEFAULT_MINIMUM_COPIES_FOR_DISCOUNT;
  }

  protected boolean isAdditionalDiscountApplicable(final Long copies) {
    return copies >= this.getMinimumCopiesForDiscount();
  }

  @Override
  public PayableAmount calculateSubtotal(final OrderItem orderItem) {
    final PayableAmount payableAmount = new PayableAmount();

    BigDecimal discount = this.getDefaultDiscount();
    final Long copies = orderItem.getAllocation().getCopies();

    if (this.isAdditionalDiscountApplicable(copies)) {
      discount = discount.multiply(this.getAdditionalDiscount());
    }

    payableAmount.setDiscount(discount);

    final BigDecimal bookPrice = orderItem.getAllocation().getBook().getPrice();
    final BigDecimal subtotal = bookPrice.multiply(BigDecimal.valueOf(copies)).multiply(discount);

    payableAmount.setSubtotal(subtotal);

    return payableAmount;
  }
}
