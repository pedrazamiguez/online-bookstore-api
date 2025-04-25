package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.BookCopyStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.DeleteFromInventoryUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteFromInventoryUseCaseImpl implements DeleteFromInventoryUseCase {

  private final AvailableBookCopiesService availableBookCopiesService;

  private final BookCopyRepository bookCopyRepository;

  @Override
  @Transactional
  public Optional<BookAllocation> deleteFromInventory(final Long bookId, final Long copies) {
    log.info("Deleting {} copies of book with ID {} from inventory", copies, bookId);

    this.availableBookCopiesService.assure(bookId, copies);
    this.bookCopyRepository.updateCopiesStatus(bookId, copies, BookCopyStatus.DELETED);
    return this.bookCopyRepository.getInventoryDetailsByBookId(bookId);
  }
}
