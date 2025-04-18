package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;

import java.util.Optional;

public interface GetBookDetailsUseCase {
  Optional<Book> getBookDetails(Long bookId);
}
