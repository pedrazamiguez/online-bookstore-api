package es.pedrazamiguez.onlinebookstore.application.usecase.inventory;

import es.pedrazamiguez.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.onlinebookstore.domain.usecase.inventory.DeleteOldCopiesFromInventoryUseCase;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteOldCopiesFromInventoryUseCaseImpl
    implements DeleteOldCopiesFromInventoryUseCase {

  private final BookCopyRepository bookCopyRepository;

  @Override
  @Transactional
  public void deleteOldCopies(final LocalDateTime olderThan) {
    log.info("Deleting old copies from inventory older than {}", olderThan);
    this.bookCopyRepository.deleteCopiesOlderThan(olderThan);
  }
}
