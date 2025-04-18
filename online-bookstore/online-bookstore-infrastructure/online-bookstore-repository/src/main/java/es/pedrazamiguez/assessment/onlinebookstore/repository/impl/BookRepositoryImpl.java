package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookRepositoryJpa;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.BookEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

  private final BookRepositoryJpa bookRepositoryJpa;

  private final BookEntityMapper bookEntityMapper;

  @Override
  public Optional<Book> findById(Long bookId) {
    final Optional<BookEntity> bookEntityFound = this.bookRepositoryJpa.findById(bookId);
    return bookEntityFound.map(this.bookEntityMapper::toDomain);
  }
}
