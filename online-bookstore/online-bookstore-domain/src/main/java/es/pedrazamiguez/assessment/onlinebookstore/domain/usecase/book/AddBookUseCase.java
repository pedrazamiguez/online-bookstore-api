package es.pedrazamiguez.api.onlinebookstore.domain.usecase.book;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Book;

@FunctionalInterface
public interface AddBookUseCase {

  Book addBook(Book book);
}
