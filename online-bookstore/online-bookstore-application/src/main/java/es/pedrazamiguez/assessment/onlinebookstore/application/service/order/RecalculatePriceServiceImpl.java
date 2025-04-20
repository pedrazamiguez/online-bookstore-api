package es.pedrazamiguez.assessment.onlinebookstore.application.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.adapter.CalculateSubtotalAdapter;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.RecalculatePriceService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecalculatePriceServiceImpl implements RecalculatePriceService {

  private final CalculateSubtotalAdapter calculateSubtotalAdapter;

  @Override
  public void recalculate(final Order order) {
    final BigDecimal totalPrice =
        order.getLines().stream()
            .map(
                line -> {
                  line.setPayableAmount(this.calculateSubtotalAdapter.calculateSubtotal(line));
                  return line.getPayableAmount().getSubtotal();
                })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    order.setTotalPrice(totalPrice);
  }
}
