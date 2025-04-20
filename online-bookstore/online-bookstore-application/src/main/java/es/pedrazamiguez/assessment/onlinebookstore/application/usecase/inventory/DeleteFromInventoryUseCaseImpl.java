package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.book.CheckAvailableBookCopiesService;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.DeleteFromInventoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteFromInventoryUseCaseImpl implements DeleteFromInventoryUseCase {

  private final CheckAvailableBookCopiesService checkAvailableBookCopiesService;

  private final BookCopyRepository bookCopyRepository;

  @Override
  @Transactional
  public Optional<BookAllocation> deleteFromInventory(final Long bookId, final Long copies) {
    log.info("Deleting {} copies of book with ID {} from inventory", copies, bookId);

    this.checkAvailableBookCopiesService.assure(bookId, copies);
    this.bookCopyRepository.deleteCopies(bookId, copies);
    return this.bookCopyRepository.getInventoryDetailsByBookId(bookId);
  }
}
