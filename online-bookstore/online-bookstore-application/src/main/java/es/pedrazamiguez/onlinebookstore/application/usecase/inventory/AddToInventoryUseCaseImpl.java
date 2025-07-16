package es.pedrazamiguez.onlinebookstore.application.usecase.inventory;

import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.onlinebookstore.domain.usecase.inventory.AddToInventoryUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddToInventoryUseCaseImpl implements AddToInventoryUseCase {

  private final BookCopyRepository bookCopyRepository;

  @Override
  public Optional<BookAllocation> addToInventory(final Long bookId, final Long copies) {
    log.info("Adding {} copies of book with ID {} to inventory", copies, bookId);
    this.bookCopyRepository.addCopies(bookId, copies);
    return this.bookCopyRepository.getInventoryDetailsByBookId(bookId);
  }
}
