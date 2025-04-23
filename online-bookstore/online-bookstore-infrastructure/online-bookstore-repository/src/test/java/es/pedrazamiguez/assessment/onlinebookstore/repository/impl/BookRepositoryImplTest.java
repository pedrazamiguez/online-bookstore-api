package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookAlreadyExistsException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.BookEntityMapper;
import java.util.Optional;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class BookRepositoryImplTest {

  @InjectMocks private BookRepositoryImpl bookRepositoryImpl;

  @Mock private BookJpaRepository bookJpaRepository;

  @Mock private BookEntityMapper bookEntityMapper;

  @Test
  @DisplayName("When book exists, findById returns book")
  void givenExistingBookId_whenFindById_thenReturnsBook() {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final BookEntity entity = Instancio.create(BookEntity.class);
    final Book expectedBook = Instancio.create(Book.class);
    when(this.bookJpaRepository.findById(bookId)).thenReturn(Optional.of(entity));
    when(this.bookEntityMapper.toDomain(entity)).thenReturn(expectedBook);

    // THEN
    final Book result = this.bookRepositoryImpl.findById(bookId);

    // THEN
    assertThat(result).isEqualTo(expectedBook);
    verify(this.bookJpaRepository).findById(bookId);
    verify(this.bookEntityMapper).toDomain(entity);
    verifyNoMoreInteractions(this.bookJpaRepository, this.bookEntityMapper);
  }

  @Test
  @DisplayName("When book is not found, findById throws BookNotFoundException")
  void givenNonExistentBookId_whenFindById_thenThrowsBookNotFoundException() {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    when(this.bookJpaRepository.findById(bookId)).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> this.bookRepositoryImpl.findById(bookId))
        .isInstanceOf(BookNotFoundException.class)
        .hasMessage("Book with ID %s not found", bookId);

    // THEN
    verify(this.bookJpaRepository).findById(bookId);
    verify(this.bookEntityMapper, never()).toDomain(any());
    verifyNoMoreInteractions(this.bookJpaRepository, this.bookEntityMapper);
  }

  @Test
  @DisplayName("When saving a new book, save returns the saved book")
  void givenValidBook_whenSave_thenReturnsSavedBook() {
    // GIVEN
    final Book inputBook = Instancio.create(Book.class);
    final BookEntity bookEntity = Instancio.create(BookEntity.class);
    final BookEntity savedBookEntity = Instancio.create(BookEntity.class);
    final Book expectedBook = Instancio.create(Book.class);

    when(this.bookEntityMapper.toEntity(inputBook)).thenReturn(bookEntity);
    when(this.bookJpaRepository.save(bookEntity)).thenReturn(savedBookEntity);
    when(this.bookEntityMapper.toDomain(savedBookEntity)).thenReturn(expectedBook);

    // WHEN
    final Book result = this.bookRepositoryImpl.save(inputBook);

    // THEN
    assertThat(result).isEqualTo(expectedBook);
    verify(this.bookEntityMapper).toEntity(inputBook);
    verify(this.bookJpaRepository).save(bookEntity);
    verify(this.bookEntityMapper).toDomain(savedBookEntity);
    verifyNoMoreInteractions(this.bookJpaRepository, this.bookEntityMapper);
  }

  @Test
  @DisplayName("When saving a book with duplicate ISBN, save throws BookAlreadyExistsException")
  void givenBookWithDuplicateIsbn_whenSave_thenThrowsBookAlreadyExistsException() {
    // GIVEN
    final Book inputBook = Instancio.create(Book.class);
    final String isbn = Instancio.create(String.class);
    final BookEntity bookEntity =
        Instancio.of(BookEntity.class).set(Select.field("isbn"), isbn).create();

    when(this.bookEntityMapper.toEntity(inputBook)).thenReturn(bookEntity);
    when(this.bookJpaRepository.save(bookEntity))
        .thenThrow(new DataIntegrityViolationException("Duplicate ISBN"));

    // WHEN
    assertThatThrownBy(() -> this.bookRepositoryImpl.save(inputBook))
        .isInstanceOf(BookAlreadyExistsException.class)
        .hasMessage("Book with ISBN %s already exists", isbn);

    // THEN
    verify(this.bookEntityMapper).toEntity(inputBook);
    verify(this.bookJpaRepository).save(bookEntity);
    verify(this.bookEntityMapper, never()).toDomain(any());
    verifyNoMoreInteractions(this.bookJpaRepository, this.bookEntityMapper);
  }
}
