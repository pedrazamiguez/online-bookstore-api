package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookRepository;
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
    assertThat(result).isNotNull();
    assertThat(book).isEqualTo(result);

    verify(this.bookRepository, times(1)).save(book);
  }
}
