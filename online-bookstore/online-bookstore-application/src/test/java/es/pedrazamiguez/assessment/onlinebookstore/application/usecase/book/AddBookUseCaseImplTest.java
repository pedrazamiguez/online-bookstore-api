package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.book;

import static org.junit.jupiter.api.Assertions.*;
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

    // WHEN
    when(this.bookRepository.save(book)).thenReturn(book);

    // THEN
    final var result = this.addBookUseCaseImpl.addBook(book);

    // ASSERT
    assertNotNull(result);
    assertEquals(book, result);

    // VERIFY
    verify(this.bookRepository, times(1)).save(book);
  }
}
