package es.pedrazamiguez.onlinebookstore.application.service.order;

import es.pedrazamiguez.onlinebookstore.domain.service.order.SubtotalPriceService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class RegularSubtotalPriceServiceImpl extends AbstractSubtotalPriceServiceImpl
    implements SubtotalPriceService {

  public RegularSubtotalPriceServiceImpl(
      final DiscountConfigurationProperties discountConfigurationProperties) {
    super(discountConfigurationProperties);
  }

  @Override
  public String getBookTypeCode() {
    return "REGULAR";
  }

  @Override
  protected BigDecimal getAdditionalDiscount() {
    return this.discountConfigurationProperties.getRegular().getBundle();
  }
}
