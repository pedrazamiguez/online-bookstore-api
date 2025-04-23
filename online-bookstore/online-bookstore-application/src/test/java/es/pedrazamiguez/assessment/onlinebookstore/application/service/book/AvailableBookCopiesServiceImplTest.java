package es.pedrazamiguez.assessment.onlinebookstore.application.service.book;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.NotEnoughBookCopiesException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AvailableBookCopiesServiceImplTest {

  @InjectMocks private AvailableBookCopiesServiceImpl availableBookCopiesService;

  @Mock private BookCopyRepository bookCopyRepository;

  @Test
  void testCheck_Ok() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;

    final Optional<BookAllocation> expectedBookAllocation =
        Optional.of(this.givenBookAllocation(bookId, requestedCopies));

    // WHEN
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(expectedBookAllocation);

    // THEN
    final boolean result = this.availableBookCopiesService.check(bookId, requestedCopies);

    // ASSERT
    assertTrue(result);

    // VERIFY
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
  }

  @Test
  void testCheck_NotEnoughCopies() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;

    final Optional<BookAllocation> expectedBookAllocation =
        Optional.of(this.givenBookAllocation(bookId, requestedCopies - 1));

    // WHEN
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(expectedBookAllocation);

    // THEN
    final boolean result = this.availableBookCopiesService.check(bookId, requestedCopies);

    // ASSERT
    assertFalse(result);

    // VERIFY
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
  }

  @Test
  void testAssure_Ok() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;

    final Optional<BookAllocation> expectedBookAllocation =
        Optional.of(this.givenBookAllocation(bookId, requestedCopies));

    // WHEN
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(expectedBookAllocation);

    // THEN & ASSERT
    assertDoesNotThrow(() -> this.availableBookCopiesService.assure(bookId, requestedCopies));

    // VERIFY
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
  }

  @Test
  void testAssure_NotEnoughCopies() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;

    final Optional<BookAllocation> expectedBookAllocation =
        Optional.of(this.givenBookAllocation(bookId, requestedCopies - 1));

    // WHEN
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(expectedBookAllocation);

    // THEN
    final NotEnoughBookCopiesException exceptionThrown =
        assertThrows(
            NotEnoughBookCopiesException.class,
            () -> this.availableBookCopiesService.assure(bookId, requestedCopies));

    // ASSERT
    assertEquals(bookId, exceptionThrown.getBookId());
    assertEquals(requestedCopies - 1, exceptionThrown.getAvailableCopies());
    assertEquals(requestedCopies, exceptionThrown.getRequestedCopies());
    assertEquals(
        String.format(
            "Not enough copies of book with ID %d. Available: %d, Requested: %d",
            bookId, requestedCopies - 1, requestedCopies),
        exceptionThrown.getMessage());

    // VERIFY
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
  }

  private Book givenBook(final Long bookId) {
    final Book book = Instancio.create(Book.class);
    book.setId(bookId);
    return book;
  }

  private BookAllocation givenBookAllocation(final Long bookId, final Long copies) {
    return Instancio.of(BookAllocation.class)
        .supply(field(BookAllocation::getBook), gen -> this.givenBook(bookId))
        .supply(field(BookAllocation::getCopies), gen -> copies)
        .create();
  }
}
