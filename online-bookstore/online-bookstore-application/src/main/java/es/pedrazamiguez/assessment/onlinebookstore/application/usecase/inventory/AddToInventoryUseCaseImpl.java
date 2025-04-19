package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.AddToInventoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddToInventoryUseCaseImpl implements AddToInventoryUseCase {

  private final BookCopyRepository bookCopyRepository;

  @Override
  public void addToInventory(final Long bookId, final int copies) {
    log.info("Adding {} copies of book with ID {} to inventory", copies, bookId);
    this.bookCopyRepository.addCopies(bookId, copies);
  }
}
