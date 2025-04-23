package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.RemoveFromOrderUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveFromOrderUseCaseImpl implements RemoveFromOrderUseCase {

  private final SecurityService securityService;

  private final AvailableBookCopiesService availableBookCopiesService;

  private final CurrentOrderService currentOrderService;

  private final OrderRepository orderRepository;

  private final FinalPriceService finalPriceService;

  @Override
  @Transactional
  public Optional<Order> removeFromOrder(final Long bookId, final Long copies) {
    final String username = this.securityService.getCurrentUserName();

    log.info("Removing {} copies of bookId {} from order for user: {}", copies, bookId, username);
    final Order existingOrder = this.currentOrderService.getOrCreateOrder(username);
    final Order updatedOrder =
            this.orderRepository.deleteOrderItem(existingOrder.getId(), bookId, copies);

    if (CollectionUtils.isEmpty(updatedOrder.getLines())) {
      return Optional.empty();
    }

    this.finalPriceService.calculate(updatedOrder);
    return Optional.of(updatedOrder);
  }
}
