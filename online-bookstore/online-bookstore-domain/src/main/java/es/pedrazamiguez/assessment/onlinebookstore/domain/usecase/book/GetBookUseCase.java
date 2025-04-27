package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;

@FunctionalInterface
public interface GetBookUseCase {

    Book getBookDetails(Long bookId);
}
