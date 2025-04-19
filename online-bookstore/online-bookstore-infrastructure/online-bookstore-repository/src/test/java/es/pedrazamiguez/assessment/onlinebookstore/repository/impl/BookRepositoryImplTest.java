package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookRepositoryJpa;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.BookEntityMapper;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookRepositoryImplTest {

  @InjectMocks private BookRepositoryImpl bookRepositoryImpl;

  @Mock private BookRepositoryJpa bookRepositoryJpa;

  @Mock private BookEntityMapper bookEntityMapper;

  @Test
  void testFindById_thenFound() {
    // GIVEN
    final var bookId = Instancio.create(Long.class);
    final var bookEntity = Instancio.create(BookEntity.class);
    final var book = Instancio.create(Book.class);

    // WHEN
    when(this.bookRepositoryJpa.findById(bookId)).thenReturn(Optional.of(bookEntity));
    when(this.bookEntityMapper.toDomain(bookEntity)).thenReturn(book);

    // THEN
    final var result = this.bookRepositoryImpl.findById(bookId);

    // ASSERT
    assertNotNull(result);
    assertEquals(book, result);

    // VERIFY
    verify(this.bookRepositoryJpa, times(1)).findById(bookId);
    verify(this.bookEntityMapper, times(1)).toDomain(bookEntity);
  }

  @Test
  void testFindById_thenNotFound() {
    // GIVEN
    final var bookId = Instancio.create(Long.class);

    // WHEN
    when(this.bookRepositoryJpa.findById(bookId)).thenReturn(Optional.empty());

    // THEN
    final BookNotFoundException exceptionThrown =
        assertThrows(BookNotFoundException.class, () -> this.bookRepositoryImpl.findById(bookId));

    // ASSERT
    assertEquals(String.format("Book with ID %s not found", bookId), exceptionThrown.getMessage());

    // VERIFY
    verify(this.bookRepositoryJpa, times(1)).findById(bookId);
    verify(this.bookEntityMapper, never()).toDomain(any());
  }
}
