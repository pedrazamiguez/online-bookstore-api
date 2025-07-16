package es.pedrazamiguez.onlinebookstore.domain.exception;

import lombok.Getter;

@Getter
public class OrderContainsNoItemsException extends RuntimeException {

  private final Long orderId;

  public OrderContainsNoItemsException(final Long orderId) {
    super("Order with id " + orderId + " contains no items for purchase");
    this.orderId = orderId;
  }
}
