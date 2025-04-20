package es.pedrazamiguez.assessment.onlinebookstore.application.adapter;

import es.pedrazamiguez.assessment.onlinebookstore.domain.adapter.CalculateSubtotalAdapter;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookType;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.PriceService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
public class CalculateSubtotalAdapterImpl implements CalculateSubtotalAdapter {

  private final Map<String, PriceService> strategiesByBookType;

  public CalculateSubtotalAdapterImpl(final List<PriceService> strategies) {
    this.strategiesByBookType =
        strategies.stream()
            .collect(Collectors.toMap(PriceService::getBookTypeCode, Function.identity()));
  }

  @Override
  public PayableAmount calculateSubtotal(final OrderItem orderItem) {
    final BookType bookType = orderItem.getAllocation().getBook().getType();
    final PriceService priceService = this.strategiesByBookType.get(bookType.getCode());

    log.info(
        "Calculating subtotal for order item: {} with strategy: {}",
        orderItem,
        priceService.getClass().getSimpleName());

    if (ObjectUtils.isEmpty(priceService)) {
      throw new IllegalArgumentException(
          String.format("No price service found for book type: %s", bookType));
    }

    return priceService.calculateSubtotal(orderItem);
  }
}
