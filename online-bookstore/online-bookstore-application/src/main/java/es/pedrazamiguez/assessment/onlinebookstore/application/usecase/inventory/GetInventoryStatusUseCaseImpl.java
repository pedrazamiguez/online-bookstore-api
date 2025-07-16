package es.pedrazamiguez.api.onlinebookstore.application.usecase.inventory;

import es.pedrazamiguez.api.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.api.onlinebookstore.domain.usecase.inventory.GetInventoryStatusUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetInventoryStatusUseCaseImpl implements GetInventoryStatusUseCase {

  private final BookCopyRepository bookCopyRepository;

  @Override
  public List<BookAllocation> getInventoryStatus(final boolean includeOutOfStock) {
    log.info("Getting inventory status with includeOutOfStock: {}", includeOutOfStock);
    return this.bookCopyRepository.getInventoryDetails(includeOutOfStock);
  }
}
