package es.pedrazamiguez.onlinebookstore.apirest.controller;

import es.pedrazamiguez.onlinebookstore.apirest.mapper.OrderRestMapper;
import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.usecase.order.AddToOrderUseCase;
import es.pedrazamiguez.onlinebookstore.domain.usecase.order.ClearOrderUseCase;
import es.pedrazamiguez.onlinebookstore.domain.usecase.order.RemoveFromOrderUseCase;
import es.pedrazamiguez.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import es.pedrazamiguez.onlinebookstore.domain.usecase.purchase.PerformPurchaseUseCase;
import es.pedrazamiguez.onlinebookstore.openapi.OrderApi;
import es.pedrazamiguez.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.onlinebookstore.openapi.model.PurchaseRequestDto;
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

  private final PerformPurchaseUseCase performPurchaseUseCase;

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

  @PreAuthorize("hasRole('USER')")
  @Override
  public ResponseEntity<OrderDto> purchaseCurrentOrder(
      final PurchaseRequestDto purchaseRequestODto) {

    final Order orderRequest = this.orderRestMapper.toDomain(purchaseRequestODto);
    final Order purchasedOrder = this.performPurchaseUseCase.purchase(orderRequest);
    return ResponseEntity.ok(this.orderRestMapper.toDto(purchasedOrder));
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
