package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;

@FunctionalInterface
public interface AddBookUseCase {

  Book addBook(Book book);
}
