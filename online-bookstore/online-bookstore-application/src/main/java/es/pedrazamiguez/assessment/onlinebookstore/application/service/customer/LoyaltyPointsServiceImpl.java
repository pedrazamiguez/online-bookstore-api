package es.pedrazamiguez.api.onlinebookstore.application.service.customer;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.service.customer.LoyaltyPointsService;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyPointsServiceImpl implements LoyaltyPointsService {

  @Override
  public Long calculateLoyaltyPoints(final Order order) {
    return order.getLines().stream()
        .map(line -> line.getAllocation().getCopies())
        .reduce(0L, Long::sum);
  }
}
