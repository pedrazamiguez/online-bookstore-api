package es.pedrazamiguez.assessment.onlinebookstore.domain.exception;

import lombok.Getter;

@Getter
public class BookNotInOrderException extends RuntimeException {

  private final Long bookId;
  private final Long orderId;

  public BookNotInOrderException(final Long bookId, final Long orderId) {
    super("Book with id " + bookId + " not in order with id " + orderId);
    this.bookId = bookId;
    this.orderId = orderId;
  }
}
