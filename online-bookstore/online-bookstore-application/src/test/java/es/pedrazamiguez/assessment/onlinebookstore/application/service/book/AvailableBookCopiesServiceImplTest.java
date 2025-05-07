package es.pedrazamiguez.assessment.onlinebookstore.application.service.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.NotEnoughBookCopiesException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("When enough copies are available, check returns true")
  void givenEnoughCopies_whenCheck_thenReturnsTrue() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;
    final BookAllocation allocation = this.createBookAllocation(bookId, requestedCopies);
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(Optional.of(allocation));

    // WHEN
    final boolean result = this.availableBookCopiesService.check(bookId, requestedCopies);

    // THEN
    assertThat(result).isTrue();
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When not enough copies are available, check returns false")
  void givenNotEnoughCopies_whenCheck_thenReturnsFalse() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;
    final BookAllocation allocation = this.createBookAllocation(bookId, requestedCopies - 1);
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(Optional.of(allocation));

    // WHEN
    final boolean result = this.availableBookCopiesService.check(bookId, requestedCopies);

    // THEN
    assertThat(result).isFalse();
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When no allocation exists, check returns false")
  void givenNoAllocation_whenCheck_thenReturnsFalse() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId)).thenReturn(Optional.empty());

    // WHEN
    final boolean result = this.availableBookCopiesService.check(bookId, requestedCopies);

    // THEN
    assertThat(result).isFalse();
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When enough copies are available, assure does not throw")
  void givenEnoughCopies_whenAssure_thenDoesNotThrow() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;
    final BookAllocation allocation = this.createBookAllocation(bookId, requestedCopies);
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(Optional.of(allocation));

    // WHEN
    assertThatCode(() -> this.availableBookCopiesService.assure(bookId, requestedCopies))
        .doesNotThrowAnyException();

    // THEN
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When no allocation exists, assure throws NotEnoughBookCopiesException")
  void givenNoAllocation_whenAssure_thenThrowsNotEnoughBookCopiesException() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId)).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> this.availableBookCopiesService.assure(bookId, requestedCopies))
        .isInstanceOf(NotEnoughBookCopiesException.class)
        .hasMessage(
            "Not enough copies of book with ID %d. Available: %d, Requested: %d",
            bookId, 0L, requestedCopies)
        .hasFieldOrPropertyWithValue("bookId", bookId)
        .hasFieldOrPropertyWithValue("availableCopies", 0L)
        .hasFieldOrPropertyWithValue("requestedCopies", requestedCopies);

    // THEN
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
  }

  @Test
  @DisplayName("When not enough copies are available, assure throws NotEnoughBookCopiesException")
  void givenNotEnoughCopies_whenAssure_thenThrowsNotEnoughBookCopiesException() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = 5L;
    final Long availableCopies = requestedCopies - 1;
    final BookAllocation allocation = this.createBookAllocation(bookId, availableCopies);
    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(Optional.of(allocation));

    // WHEN
    assertThatThrownBy(() -> this.availableBookCopiesService.assure(bookId, requestedCopies))
        .isInstanceOf(NotEnoughBookCopiesException.class)
        .hasMessage(
            "Not enough copies of book with ID %d. Available: %d, Requested: %d",
            bookId, availableCopies, requestedCopies)
        .hasFieldOrPropertyWithValue("bookId", bookId)
        .hasFieldOrPropertyWithValue("availableCopies", availableCopies)
        .hasFieldOrPropertyWithValue("requestedCopies", requestedCopies);

    // THEN
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When bookId is null, check returns false")
  void givenNullBookId_whenCheck_thenReturnsFalse() {
    // GIVEN
    final Long requestedCopies = 5L;

    // WHEN
    final boolean result = this.availableBookCopiesService.check(null, requestedCopies);

    // THEN
    assertThat(result).isFalse();
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When requestedCopies is null, check returns false")
  void givenNullRequestedCopies_whenCheck_thenReturnsFalse() {
    // GIVEN
    final Long bookId = 1L;

    // WHEN
    final boolean result = this.availableBookCopiesService.check(bookId, null);

    // THEN
    assertThat(result).isFalse();
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When requestedCopies is negative, check returns false")
  void givenNegativeRequestedCopies_whenCheck_thenReturnsFalse() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = -1L;

    // WHEN
    final boolean result = this.availableBookCopiesService.check(bookId, requestedCopies);

    // THEN
    assertThat(result).isFalse();
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When bookId is null, assure throws IllegalArgumentException")
  void givenNullBookId_whenAssure_thenThrowsIllegalArgumentException() {
    // GIVEN
    final Long requestedCopies = 5L;

    // WHEN
    assertThatThrownBy(() -> this.availableBookCopiesService.assure(null, requestedCopies))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bookId cannot be null");

    // THEN
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When requestedCopies is null, assure throws IllegalArgumentException")
  void givenNullRequestedCopies_whenAssure_thenThrowsIllegalArgumentException() {
    // GIVEN
    final Long bookId = 1L;

    // WHEN
    assertThatThrownBy(() -> this.availableBookCopiesService.assure(bookId, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("requestedCopies cannot be null");

    // THEN
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  @DisplayName("When requestedCopies is negative, assure throws IllegalArgumentException")
  void givenNegativeRequestedCopies_whenAssure_thenThrowsIllegalArgumentException() {
    // GIVEN
    final Long bookId = 1L;
    final Long requestedCopies = -1L;

    // WHEN
    assertThatThrownBy(() -> this.availableBookCopiesService.assure(bookId, requestedCopies))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("requestedCopies cannot be negative");

    // THEN
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  private BookAllocation createBookAllocation(final Long bookId, final Long copies) {
    final Book book = new Book();
    book.setId(bookId);
    final BookAllocation allocation = new BookAllocation();
    allocation.setBook(book);
    allocation.setCopies(copies);
    return allocation;
  }
}
