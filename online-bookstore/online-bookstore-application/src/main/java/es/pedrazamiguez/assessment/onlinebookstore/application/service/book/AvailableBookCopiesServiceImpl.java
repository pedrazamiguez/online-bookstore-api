package es.pedrazamiguez.assessment.onlinebookstore.application.service.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.NotEnoughBookCopiesException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailableBookCopiesServiceImpl implements AvailableBookCopiesService {

  private final BookCopyRepository bookCopyRepository;

  @Override
  public boolean check(final Long bookId, final Long requestedCopies) {

    if (bookId == null) {
      return false;
    }

    if (requestedCopies == null || requestedCopies < 0) {
      return false;
    }

    final Optional<BookAllocation> bookAllocation =
        this.bookCopyRepository.getInventoryDetailsByBookId(bookId);

    return bookAllocation.isPresent() && bookAllocation.get().getCopies() >= requestedCopies;
  }

  @Override
  public void assure(final Long bookId, final Long requestedCopies)
      throws NotEnoughBookCopiesException {

    if (bookId == null) {
      throw new IllegalArgumentException("bookId cannot be null");
    }

    if (requestedCopies == null) {
      throw new IllegalArgumentException("requestedCopies cannot be null");
    }

    if (requestedCopies < 0) {
      throw new IllegalArgumentException("requestedCopies cannot be negative");
    }

    final Optional<BookAllocation> bookAllocation =
        this.bookCopyRepository.getInventoryDetailsByBookId(bookId);

    if (bookAllocation.isEmpty()) {
      throw new NotEnoughBookCopiesException(bookId, 0L, requestedCopies);
    }

    final BookAllocation allocation = bookAllocation.get();
    if (allocation.getCopies() < requestedCopies) {
      throw new NotEnoughBookCopiesException(bookId, allocation.getCopies(), requestedCopies);
    }
  }
}
