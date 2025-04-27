package es.pedrazamiguez.assessment.onlinebookstore.application.processor.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.BookCopyStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PurchaseStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryUpdateProcessor implements PurchaseProcessor {

  private final BookCopyRepository bookCopyRepository;

  @Override
  public void process(final PurchaseContext context) {
    final var purchasedOrder = context.getPurchasedOrder();

    log.info("Updating inventory for orderId {}", purchasedOrder.getId());
    purchasedOrder
        .getLines()
        .forEach(
            orderItem -> {
              final Long bookId = orderItem.getAllocation().getBook().getId();
              final Long copies = orderItem.getAllocation().getCopies();
              this.bookCopyRepository.updateCopiesStatus(bookId, copies, BookCopyStatus.SOLD);
            });

    context.setStatus(PurchaseStatus.SUCCESS);
  }
}
