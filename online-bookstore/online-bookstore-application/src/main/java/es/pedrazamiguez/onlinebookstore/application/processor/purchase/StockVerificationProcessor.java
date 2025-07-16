package es.pedrazamiguez.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockVerificationProcessor implements PurchaseProcessor {

  private final AvailableBookCopiesService availableBookCopiesService;

  @Override
  public void process(final PurchaseContext context) {
    final var existingOrder = context.getExistingOrder();
    log.info("Verifying stock for orderId {}", existingOrder.getId());
    existingOrder.getLines().stream()
        .map(OrderItem::getAllocation)
        .forEach(
            allocation ->
                this.availableBookCopiesService.assure(
                    allocation.getBook().getId(), allocation.getCopies()));
  }
}
