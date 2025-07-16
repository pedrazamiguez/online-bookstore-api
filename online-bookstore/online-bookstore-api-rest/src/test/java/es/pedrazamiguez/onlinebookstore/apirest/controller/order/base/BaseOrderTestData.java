package es.pedrazamiguez.onlinebookstore.apirest.controller.order.base;

import static org.instancio.Select.field;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.onlinebookstore.openapi.model.PurchaseRequestDto;
import java.util.Optional;
import org.instancio.Instancio;

public abstract class BaseOrderTestData {

  protected Order givenOrder() {
    return this.givenOrder(null);
  }

  protected Order givenOrder(final Long id) {
    return Instancio.of(Order.class)
        .generate(field("id"), gen -> gen.longs().min(id != null ? id : 1))
        .set(field("id"), id)
        .create();
  }

  protected OrderDto givenOrderDto() {
    return this.givenOrderDto(null);
  }

  protected OrderDto givenOrderDto(final Long id) {
    return Instancio.of(OrderDto.class)
        .generate(field("id"), gen -> gen.longs().min(id != null ? id : 1))
        .set(field("id"), id)
        .create();
  }

  protected AllocationDto givenAllocationDto() {
    return Instancio.of(AllocationDto.class)
        .generate(field("copies"), gen -> gen.longs().range(1L, 100L))
        .create();
  }

  protected PurchaseRequestDto givenPurchaseRequestDto() {
    return Instancio.of(PurchaseRequestDto.class).create();
  }

  protected Optional<Order> givenOptionalOrder() {
    return Optional.of(this.givenOrder());
  }

  protected Optional<Order> givenEmptyOptionalOrder() {
    return Optional.empty();
  }
}
