package es.pedrazamiguez.onlinebookstore.domain.exception;

import lombok.Getter;

@Getter
public class NotEnoughBookCopiesException extends RuntimeException {

  private final Long bookId;
  private final Long availableCopies;
  private final Long requestedCopies;

  public NotEnoughBookCopiesException(
      final Long bookId, final Long availableCopies, final Long requestedCopies) {

    super(
        "Not enough copies of book with ID "
            + bookId
            + ". Available: "
            + availableCopies
            + ", Requested: "
            + requestedCopies);

    this.bookId = bookId;
    this.availableCopies = availableCopies;
    this.requestedCopies = requestedCopies;
  }
}
