package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OldEditionSubtotalPriceServiceImpl implements SubtotalPriceService {

  private static final BigDecimal DEFAULT_DISCOUNT = new BigDecimal("0.8");
    private static final BigDecimal APPLICABLE_DISCOUNT = new BigDecimal("0.95");
    private static final Long MINIMUM_COPIES_FOR_DISCOUNT = 3L;

    @Override
    public String getBookTypeCode() {
        return "OLD_EDITION";
    }

    @Override
    public PayableAmount calculateSubtotal(final OrderItem orderItem) {
      final PayableAmount payableAmount = new PayableAmount();

      final Long copies = orderItem.getAllocation().getCopies();

      if (copies >= MINIMUM_COPIES_FOR_DISCOUNT) {
        payableAmount.setDiscount(DEFAULT_DISCOUNT.multiply(APPLICABLE_DISCOUNT));
      } else {
        payableAmount.setDiscount(DEFAULT_DISCOUNT);
      }

      final BigDecimal bookPrice = orderItem.getAllocation().getBook().getPrice();

      final BigDecimal subtotal =
              bookPrice.multiply(BigDecimal.valueOf(copies)).multiply(payableAmount.getDiscount());

      payableAmount.setSubtotal(subtotal);

      return payableAmount;
    }
}
