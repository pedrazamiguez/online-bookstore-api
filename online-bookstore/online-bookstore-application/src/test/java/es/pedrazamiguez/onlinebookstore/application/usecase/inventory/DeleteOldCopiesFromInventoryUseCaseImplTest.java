package es.pedrazamiguez.onlinebookstore.application.usecase.inventory;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import es.pedrazamiguez.onlinebookstore.domain.repository.BookCopyRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteOldCopiesFromInventoryUseCaseImplTest {

  @InjectMocks private DeleteOldCopiesFromInventoryUseCaseImpl deleteOldCopiesFromInventoryUseCase;

  @Mock private BookCopyRepository bookCopyRepository;

  @Test
  void givenOlderThanDate_whenDeleteOldCopies_thenDeleteOldCopies() {
    // GIVEN
    final LocalDateTime olderThan = LocalDateTime.now().minusDays(30);

    // WHEN
    this.deleteOldCopiesFromInventoryUseCase.deleteOldCopies(olderThan);

    // THEN
    verify(this.bookCopyRepository).deleteCopiesOlderThan(olderThan);
    verifyNoMoreInteractions(this.bookCopyRepository);
  }
}
