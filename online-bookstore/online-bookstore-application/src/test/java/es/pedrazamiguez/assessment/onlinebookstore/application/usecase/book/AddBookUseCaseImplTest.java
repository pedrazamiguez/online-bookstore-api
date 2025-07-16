package es.pedrazamiguez.api.onlinebookstore.application.usecase.book;

import static org.assertj.core.api.Assertions.assertThat;
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
class AddBookUseCaseImplTest {

  @InjectMocks private AddBookUseCaseImpl addBookUseCaseImpl;

  @Mock private BookRepository bookRepository;

  @Test
  void testAddBook() {
    // GIVEN
    final var book = Instancio.create(Book.class);
    when(this.bookRepository.save(book)).thenReturn(book);

    // WHEN
    final var result = this.addBookUseCaseImpl.addBook(book);

    // THEN
    assertThat(result).isNotNull().isEqualTo(book);

    verify(this.bookRepository, times(1)).save(book);
  }
}
