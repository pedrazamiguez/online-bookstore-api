package es.pedrazamiguez.onlinebookstore.application.usecase.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import es.pedrazamiguez.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.onlinebookstore.domain.service.security.SecurityService;
import es.pedrazamiguez.onlinebookstore.domain.usecase.order.RemoveFromOrderUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
