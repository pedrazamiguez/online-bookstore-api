package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class OldEditionSubtotalPriceServiceImpl extends AbstractSubtotalPriceServiceImpl
        implements SubtotalPriceService {

    public OldEditionSubtotalPriceServiceImpl(
            final DiscountConfigurationProperties discountConfigurationProperties) {
        super(discountConfigurationProperties);
    }

    @Override
    public String getBookTypeCode() {
        return "OLD_EDITION";
    }

    @Override
    public BigDecimal getDefaultDiscount() {
        return this.discountConfigurationProperties.getOldEdition().getDefaultDiscount();
    }

    @Override
    protected BigDecimal getAdditionalDiscount() {
        return this.discountConfigurationProperties.getOldEdition().getBundle();
    }
}
