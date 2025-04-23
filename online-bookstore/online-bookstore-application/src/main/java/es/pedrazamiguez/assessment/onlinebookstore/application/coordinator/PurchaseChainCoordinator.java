package es.pedrazamiguez.assessment.onlinebookstore.application.coordinator;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PurchaseStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.PurchaseException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PurchaseChainCoordinator {

  private final List<PurchaseProcessor> processors;

  @Transactional(rollbackFor = PurchaseException.class)
  public PurchaseContext executeChain(final String userId, final Long orderId) {
    final PurchaseContext context = new PurchaseContext();

    context.setUserId(userId);
    final Order order = new Order();
    order.setId(orderId);
    context.setOrder(order);

    for (final PurchaseProcessor processor : this.processors) {

      if (!context.isSuccessful()) {
        break;
      }

      try {
        processor.process(context);
      } catch (final Exception e) {
        context.setStatus(PurchaseStatus.FAILED);
        context.setErrorMessage(e.getMessage());
        throw new PurchaseException(context, "Purchase failed: " + e.getMessage(), e);
      }
    }

    return context;
  }
}
