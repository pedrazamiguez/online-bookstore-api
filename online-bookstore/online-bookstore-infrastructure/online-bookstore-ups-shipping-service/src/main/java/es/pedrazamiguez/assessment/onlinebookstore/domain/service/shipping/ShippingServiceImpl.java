package es.pedrazamiguez.assessment.onlinebookstore.domain.service.shipping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShippingServiceImpl implements ShippingService {

  @Override
  public void processShipping(final String shippingAddress, final Long orderId) {

    log.info("Processing shipping for orderId {} to address {}", orderId, shippingAddress);
  }
}
