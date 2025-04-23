package es.pedrazamiguez.assessment.onlinebookstore.application.adapter;

import es.pedrazamiguez.assessment.onlinebookstore.domain.adapter.CalculateSubtotalAdapter;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookType;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PayableAmount;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.SubtotalPriceService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
public class CalculateSubtotalAdapterImpl implements CalculateSubtotalAdapter {

  private final Map<String, SubtotalPriceService> strategiesByBookType;

  public CalculateSubtotalAdapterImpl(final List<SubtotalPriceService> strategies) {
    if (strategies == null) {
      throw new IllegalArgumentException("Strategies list cannot be null");
    }

    // Validate for null elements
    strategies.forEach(
        strategy -> {
          if (Objects.isNull(strategy)) {
            throw new IllegalArgumentException("Strategies list cannot contain null elements");
          }
        });

    this.strategiesByBookType =
        strategies.stream()
            .collect(
                Collectors.toMap(
                    SubtotalPriceService::getBookTypeCode,
                    Function.identity(),
                    (existing, replacement) -> {
                      throw new IllegalArgumentException(
                          "Duplicate book type code: " + existing.getBookTypeCode());
                    }));
  }

  @Override
  public PayableAmount calculateSubtotal(final OrderItem orderItem) {
    if (orderItem == null
        || orderItem.getAllocation() == null
        || orderItem.getAllocation().getBook() == null) {
      throw new IllegalArgumentException("Order item or its allocation/book cannot be null");
    }

    final BookType bookType = orderItem.getAllocation().getBook().getType();
    final String bookTypeCode = bookType != null ? bookType.getCode() : null;
    final SubtotalPriceService subtotalPriceService =
        bookTypeCode != null ? this.strategiesByBookType.get(bookTypeCode) : null;

    log.info(
        "Calculating subtotal for order item: {} with strategy: {}",
        orderItem,
        Objects.isNull(subtotalPriceService)
            ? "unknown"
            : subtotalPriceService.getClass().getSimpleName());

    if (ObjectUtils.isEmpty(subtotalPriceService)) {
      throw new IllegalArgumentException(
          String.format(
              "No price service found for book type: %s", bookType != null ? bookType : "null"));
    }

    return subtotalPriceService.calculateSubtotal(orderItem);
  }
}
