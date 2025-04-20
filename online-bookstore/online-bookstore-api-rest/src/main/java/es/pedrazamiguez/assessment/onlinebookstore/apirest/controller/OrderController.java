package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.OrderRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.AddToOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.OrderApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {

  private final ViewOrderUseCase viewOrderUseCase;

  private final AddToOrderUseCase addToOrderUseCase;

  private final OrderRestMapper orderRestMapper;

  @Override
  public ResponseEntity<OrderDto> addBookToCurrentOrder(
      final Long bookId, final AllocationDto allocationDto) {

    final Order order = this.addToOrderUseCase.addToOrder(bookId, allocationDto.getCopies());
    return ResponseEntity.ok(this.orderRestMapper.toDto(order));
  }

  @Override
  public ResponseEntity<Void> clearCurrentOrder() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  public ResponseEntity<OrderDto> getCurrentOrder() {
    return this.viewOrderUseCase
        .getCurrentOrderForCustomer()
        .map(this.orderRestMapper::toDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  @Override
  public ResponseEntity<Void> getOrderById(final Long orderId) {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  public ResponseEntity<Void> getOrderHistory() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  public ResponseEntity<Void> purchaseCurrentOrder() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  public ResponseEntity<Void> removeBookFromCurrentOrder(final Long bookId) {
    throw new NotImplementedException("Not implemented yet");
  }
}
