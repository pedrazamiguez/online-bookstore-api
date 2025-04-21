package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.OrderRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.AddToOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ClearOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.RemoveFromOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.OrderApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

  private final ViewOrderUseCase viewOrderUseCase;

  private final AddToOrderUseCase addToOrderUseCase;

  private final ClearOrderUseCase clearOrderUseCase;

  private final RemoveFromOrderUseCase removeFromOrderUseCase;

  private final OrderRestMapper orderRestMapper;

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<OrderDto> addBookToCurrentOrder(
      final Long bookId, final AllocationDto allocationDto) {

    final Order order = this.addToOrderUseCase.addToOrder(bookId, allocationDto.getCopies());
    return ResponseEntity.ok(this.orderRestMapper.toDto(order));
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> clearCurrentOrder() {
    this.clearOrderUseCase.clearOrderItems();
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<OrderDto> getCurrentOrder() {
    return this.viewOrderUseCase
        .getCurrentOrderForCustomer()
        .map(this.orderRestMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> getOrderById(final Long orderId) {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> getOrderHistory() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> purchaseCurrentOrder() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<OrderDto> removeBookFromCurrentOrder(final Long bookId, final Long copies) {
    return this.removeFromOrderUseCase
        .removeFromOrder(bookId, copies)
        .map(this.orderRestMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }
}
