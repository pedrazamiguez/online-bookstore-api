package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.GetBookDetailsUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetBookDetailsUseCaseImpl implements GetBookDetailsUseCase {

  private final BookRepository bookRepository;

  @Override
  public Optional<Book> getBookDetails(Long bookId) {
    log.info("Fetching book details for bookId: {}", bookId);
    return this.bookRepository.findById(bookId);
  }
}
