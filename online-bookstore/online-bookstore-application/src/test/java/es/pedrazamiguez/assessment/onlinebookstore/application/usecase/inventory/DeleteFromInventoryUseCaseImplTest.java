package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.BookCopyStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteFromInventoryUseCaseImplTest {

  @InjectMocks private DeleteFromInventoryUseCaseImpl deleteFromInventoryUseCase;

  @Mock private AvailableBookCopiesService availableBookCopiesService;

  @Mock private BookCopyRepository bookCopyRepository;

  @Test
  void givenBookIdAndCopies_whenDeleteFromInventory_thenReturnBookAllocation() {
    // GIVEN
    final Long bookId = 1L;
    final Long copies = 2L;

    final BookAllocation bookAllocation =
        Instancio.of(BookAllocation.class)
            .set(field(BookAllocation::getCopies), copies)
            .set(
                field(BookAllocation::getBook),
                Instancio.of(Book.class).set(field(Book::getId), bookId).create())
            .set(field(BookAllocation::getStatus), BookCopyStatus.DELETED)
            .create();

    when(this.bookCopyRepository.getInventoryDetailsByBookId(bookId))
        .thenReturn(Optional.of(bookAllocation));

    // WHEN
    final Optional<BookAllocation> result =
        this.deleteFromInventoryUseCase.deleteFromInventory(bookId, copies);

    // THEN
    assertThat(result).isPresent();
    assertThat(result.get().getCopies()).isEqualTo(copies);
    assertThat(result.get().getBook().getId()).isEqualTo(bookId);
    assertThat(result.get().getStatus()).isEqualTo(BookCopyStatus.DELETED);

    verify(this.availableBookCopiesService).assure(bookId, copies);
    verify(this.bookCopyRepository).updateCopiesStatus(bookId, copies, BookCopyStatus.DELETED);
    verify(this.bookCopyRepository).getInventoryDetailsByBookId(bookId);
    verifyNoMoreInteractions(
        this.availableBookCopiesService, this.bookCopyRepository, this.bookCopyRepository);
  }
}
