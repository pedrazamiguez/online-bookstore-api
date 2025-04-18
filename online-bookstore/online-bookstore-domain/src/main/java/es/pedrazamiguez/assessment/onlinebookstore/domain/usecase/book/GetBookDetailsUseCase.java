package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import java.util.Optional;

@FunctionalInterface
public interface GetBookDetailsUseCase {
  Optional<Book> getBookDetails(Long bookId);
}
