package es.pedrazamiguez.api.onlinebookstore.application.usecase.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.BookRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBookUseCaseImplTest {

  @InjectMocks private GetBookUseCaseImpl getBookUseCase;

  @Mock private BookRepository bookRepository;

  @Test
  void givenBookId_whenGetBookDetails_thenGetBookDetails() {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final Book expectedBook = Instancio.of(Book.class).set(field(Book::getId), bookId).create();

    when(this.bookRepository.findById(bookId)).thenReturn(expectedBook);

    // WHEN
    final Book result = this.getBookUseCase.getBookDetails(bookId);

    // THEN
    assertThat(result).isEqualTo(expectedBook);
    assertThat(result.getId()).isEqualTo(bookId);
    verify(this.bookRepository).findById(bookId);
    verifyNoMoreInteractions(this.bookRepository);
  }
}
