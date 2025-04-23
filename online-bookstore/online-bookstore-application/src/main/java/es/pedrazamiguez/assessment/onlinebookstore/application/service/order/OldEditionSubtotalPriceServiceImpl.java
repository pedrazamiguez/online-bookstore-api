package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class OldEditionSubtotalPriceServiceImpl extends AbstractSubtotalPriceServiceImpl
    implements SubtotalPriceService {

  private static final BigDecimal DEFAULT_DISCOUNT = new BigDecimal("0.8");
  private static final BigDecimal ADDITIONAL_DISCOUNT = new BigDecimal("0.95");

  @Override
  public String getBookTypeCode() {
    return "OLD_EDITION";
  }

  @Override
  public BigDecimal getDefaultDiscount() {
    return DEFAULT_DISCOUNT;
  }

  @Override
  protected BigDecimal getAdditionalDiscount() {
    return ADDITIONAL_DISCOUNT;
  }
}
