package es.pedrazamiguez.api.onlinebookstore.application.service.order;

import es.pedrazamiguez.api.onlinebookstore.domain.adapter.CalculateSubtotalAdapter;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.FinalPriceService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinalPriceServiceImpl implements FinalPriceService {

  private final CalculateSubtotalAdapter calculateSubtotalAdapter;

  @Override
  public void calculate(final Order order) {
    log.info("Calculating final price for orderId {}", order.getId());

    if (CollectionUtils.isEmpty(order.getLines())) {
      log.warn("Order {} has no lines to calculate final price", order.getId());
      order.setTotalPrice(BigDecimal.ZERO);
      return;
    }

    final BigDecimal totalPrice =
        order.getLines().stream()
            .map(
                line -> {
                  line.setPayableAmount(this.calculateSubtotalAdapter.calculateSubtotal(line));
                  return line.getPayableAmount().getSubtotal();
                })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    order.setTotalPrice(totalPrice);

    log.info("Final price for orderId {} is {}", order.getId(), totalPrice);
  }
}
