package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.PriceService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegularPriceServiceImpl implements PriceService {

  private static final BigDecimal NO_DISCOUNT = BigDecimal.ONE;
  private static final BigDecimal APPLICABLE_DISCOUNT = new BigDecimal("0.9");
  private static final Long MINIMUM_COPIES_FOR_DISCOUNT = 3L;

  @Override
  public String getBookTypeCode() {
    return "REGULAR";
  }

  @Override
  public PayableAmount calculateSubtotal(final OrderItem orderItem) {
    final PayableAmount payableAmount = new PayableAmount();

    final Long copies = orderItem.getAllocation().getCopies();

    if (copies >= MINIMUM_COPIES_FOR_DISCOUNT) {
      payableAmount.setDiscount(APPLICABLE_DISCOUNT);
    } else {
      payableAmount.setDiscount(NO_DISCOUNT);
    }

    final BigDecimal bookPrice = orderItem.getAllocation().getBook().getPrice();

    final BigDecimal subtotal =
        bookPrice.multiply(BigDecimal.valueOf(copies)).multiply(payableAmount.getDiscount());

    payableAmount.setSubtotal(subtotal);

    return payableAmount;
  }
}
