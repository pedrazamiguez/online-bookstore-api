package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookRepositoryJpa;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.BookEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

  private final BookRepositoryJpa bookRepositoryJpa;

  private final BookEntityMapper bookEntityMapper;

  @Override
  public Book findById(final Long bookId) {
    return this.bookRepositoryJpa
        .findById(bookId)
        .map(this.bookEntityMapper::toDomain)
        .orElseThrow(() -> new BookNotFoundException(bookId));
  }

  @Override
  public Book save(final Book book) {
    final var bookEntityToSave = this.bookEntityMapper.toEntity(book);
    final var bookEntitySaved = this.bookRepositoryJpa.save(bookEntityToSave);
    return this.bookEntityMapper.toDomain(bookEntitySaved);
  }
}
