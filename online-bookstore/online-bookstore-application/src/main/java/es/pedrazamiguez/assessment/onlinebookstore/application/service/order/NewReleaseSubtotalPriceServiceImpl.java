package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import org.springframework.stereotype.Service;

@Service
public class NewReleaseSubtotalPriceServiceImpl extends AbstractSubtotalPriceServiceImpl
    implements SubtotalPriceService {

  @Override
  public String getBookTypeCode() {
    return "NEW_RELEASE";
  }

  @Override
  protected boolean isAdditionalDiscountApplicable(final Long copies) {
    return false;
  }
}
