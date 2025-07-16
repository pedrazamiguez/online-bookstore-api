package es.pedrazamiguez.api.onlinebookstore.application.service.order;

import es.pedrazamiguez.api.onlinebookstore.domain.service.order.SubtotalPriceService;
import org.springframework.stereotype.Service;

@Service
public class NewReleaseSubtotalPriceServiceImpl extends AbstractSubtotalPriceServiceImpl
    implements SubtotalPriceService {

  public NewReleaseSubtotalPriceServiceImpl(
      final DiscountConfigurationProperties discountConfigurationProperties) {
    super(discountConfigurationProperties);
  }

  @Override
  public String getBookTypeCode() {
    return "NEW_RELEASE";
  }

  @Override
  protected boolean isAdditionalDiscountApplicable(final Long copies) {
    return false;
  }
}
