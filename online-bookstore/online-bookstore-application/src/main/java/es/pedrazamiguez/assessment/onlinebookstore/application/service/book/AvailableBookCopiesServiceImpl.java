package es.pedrazamiguez.assessment.onlinebookstore.application.service.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.NotEnoughBookCopiesException;
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
  public boolean check(final Long bookId, final Long copies) {
    try {
      this.assure(bookId, copies);
    } catch (final NotEnoughBookCopiesException e) {
      return false;
    }
    return true;
  }

  @Override
  public void assure(final Long bookId, final Long copies) throws NotEnoughBookCopiesException {
    final Optional<BookAllocation> bookAllocation =
        this.bookCopyRepository.getInventoryDetailsByBookId(bookId);

    if (bookAllocation.isEmpty()) {
      throw new NotEnoughBookCopiesException(bookId, 0L, copies);
    }

    final BookAllocation allocation = bookAllocation.get();
    if (allocation.getCopies() < copies) {
      throw new NotEnoughBookCopiesException(bookId, allocation.getCopies(), copies);
    }
  }
}
