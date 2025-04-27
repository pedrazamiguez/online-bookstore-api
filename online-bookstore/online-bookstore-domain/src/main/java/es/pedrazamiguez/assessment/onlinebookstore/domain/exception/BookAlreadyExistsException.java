package es.pedrazamiguez.assessment.onlinebookstore.domain.exception;

import lombok.Getter;

@Getter
public class BookAlreadyExistsException extends RuntimeException {

  private final String isbn;

  public BookAlreadyExistsException(final String isbn) {
    super("Book with ISBN " + isbn + " already exists");
    this.isbn = isbn;
  }
}
