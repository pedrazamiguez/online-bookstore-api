package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewReleaseSubtotalPriceServiceImpl implements SubtotalPriceService {

  private static final BigDecimal NO_DISCOUNT = BigDecimal.ONE;

  @Override
  public String getBookTypeCode() {
    return "NEW_RELEASE";
  }

  @Override
  public PayableAmount calculateSubtotal(final OrderItem orderItem) {
    final PayableAmount payableAmount = new PayableAmount();

    payableAmount.setDiscount(NO_DISCOUNT);

    final Long copies = orderItem.getAllocation().getCopies();
    final BigDecimal bookPrice = orderItem.getAllocation().getBook().getPrice();
    final BigDecimal subtotal = bookPrice.multiply(BigDecimal.valueOf(copies));

    payableAmount.setSubtotal(subtotal);

    return payableAmount;
  }
}
