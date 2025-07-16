package es.pedrazamiguez.api.onlinebookstore.domain.repository;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Book;

public interface BookRepository {

  Book findById(Long bookId);

  Book save(Book book);
}
