package es.pedrazamiguez.onlinebookstore.application.usecase.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.onlinebookstore.domain.repository.BookCopyRepository;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddToInventoryUseCaseImplTest {

  private static final Long BOOK_ID = 1L;

  @InjectMocks private AddToInventoryUseCaseImpl addToInventoryUseCase;

  @Mock private BookCopyRepository bookCopyRepository;

  @ParameterizedTest
  @ValueSource(longs = {1, 5, 100})
  void givenExistingBookAndPositiveCopies_whenAddToInventory_thenReturnsUpdatedAllocation(
      final Long copies) {
    // GIVEN
    final BookAllocation existingAllocation = this.createBookAllocation(copies);
    when(this.bookCopyRepository.getInventoryDetailsByBookId(BOOK_ID))
        .thenReturn(Optional.of(existingAllocation));

    // WHEN
    final Optional<BookAllocation> updatedAllocation =
        this.addToInventoryUseCase.addToInventory(BOOK_ID, copies);

    // THEN
    assertThat(updatedAllocation)
        .isPresent()
        .satisfies(
            allocation -> {
              assertThat(allocation.get().getBook().getId()).isEqualTo(BOOK_ID);
              assertThat(allocation.get().getCopies()).isEqualTo(copies);
            });

    verify(this.bookCopyRepository).addCopies(BOOK_ID, copies);
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(BOOK_ID);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  @Test
  void givenNonExistingBook_whenAddToInventory_thenReturnsEmpty() {
    // GIVEN
    final Long copies = 5L;
    when(this.bookCopyRepository.getInventoryDetailsByBookId(BOOK_ID)).thenReturn(Optional.empty());

    // WHEN
    final Optional<BookAllocation> updatedAllocation =
        this.addToInventoryUseCase.addToInventory(BOOK_ID, copies);

    // THEN
    assertThat(updatedAllocation).isEmpty();
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(BOOK_ID);
    verify(this.bookCopyRepository).addCopies(BOOK_ID, copies);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }

  private BookAllocation createBookAllocation(final Long copies) {
    return Instancio.of(BookAllocation.class)
        .set(
            field(BookAllocation::getBook),
            Instancio.of(Book.class).set(field(Book::getId), BOOK_ID).create())
        .set(field(BookAllocation::getCopies), copies)
        .create();
  }
}
