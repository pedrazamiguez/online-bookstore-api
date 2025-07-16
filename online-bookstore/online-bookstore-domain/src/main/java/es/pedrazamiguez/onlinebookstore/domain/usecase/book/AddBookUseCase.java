package es.pedrazamiguez.onlinebookstore.domain.usecase.book;

import es.pedrazamiguez.onlinebookstore.domain.model.Book;

@FunctionalInterface
public interface AddBookUseCase {

  Book addBook(Book book);
}
