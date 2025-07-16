package es.pedrazamiguez.api.onlinebookstore.domain.exception;

import lombok.Getter;

@Getter
public class BookNotFoundException extends RuntimeException {

  private final Long bookId;

  public BookNotFoundException(Long bookId) {
    super("Book with ID " + bookId + " not found");
    this.bookId = bookId;
  }
}
