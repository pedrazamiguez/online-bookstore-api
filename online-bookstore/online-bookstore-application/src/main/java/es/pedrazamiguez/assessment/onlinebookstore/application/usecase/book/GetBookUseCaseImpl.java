package es.pedrazamiguez.api.onlinebookstore.application.usecase.book;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.BookRepository;
import es.pedrazamiguez.api.onlinebookstore.domain.usecase.book.GetBookUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetBookUseCaseImpl implements GetBookUseCase {

  private final BookRepository bookRepository;

  @Override
  public Book getBookDetails(final Long bookId) {
    log.info("Fetching book details for bookId: {}", bookId);
    return this.bookRepository.findById(bookId);
  }
}
