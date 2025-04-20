package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.PurchaseApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PurchaseController implements PurchaseApi {

  private final ViewOrderUseCase viewOrderUseCase;

  @Override
  public ResponseEntity<OrderDto> getCurrentOrder() {
    return this.viewOrderUseCase
        .getCurrentOrderForCustomer()
        .map(order -> ResponseEntity.ok(new OrderDto()))
        .orElse(ResponseEntity.noContent().build());
  }
}
