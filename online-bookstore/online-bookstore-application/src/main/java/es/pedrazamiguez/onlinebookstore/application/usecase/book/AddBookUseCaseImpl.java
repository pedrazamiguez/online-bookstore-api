package es.pedrazamiguez.onlinebookstore.application.usecase.book;

import es.pedrazamiguez.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.onlinebookstore.domain.repository.BookRepository;
import es.pedrazamiguez.onlinebookstore.domain.usecase.book.AddBookUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddBookUseCaseImpl implements AddBookUseCase {

  private final BookRepository bookRepository;

  @Override
  public Book addBook(final Book book) {
    log.info("Adding book: {}", book);
    return this.bookRepository.save(book);
  }
}
