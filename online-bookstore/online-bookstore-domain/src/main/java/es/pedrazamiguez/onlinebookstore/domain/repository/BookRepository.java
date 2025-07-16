package es.pedrazamiguez.onlinebookstore.domain.repository;

import es.pedrazamiguez.onlinebookstore.domain.model.Book;

public interface BookRepository {

  Book findById(Long bookId);

  Book save(Book book);
}
