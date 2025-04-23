package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.BookEntityMapper;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    // WHEN
    final Book result = this.bookRepositoryImpl.findById(bookId);

    // THEN
    assertThat(result).isEqualTo(expectedBook);
    verify(this.bookJpaRepository).findById(bookId);
    verify(this.bookEntityMapper).toDomain(entity);
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
  }
}
