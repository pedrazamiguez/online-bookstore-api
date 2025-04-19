package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.InventoryDetails;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.GetInventoryStatusUseCase;
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
  public List<InventoryDetails> getInventoryStatus(final boolean retrieveOutOfStock) {
    return this.bookCopyRepository.getInventoryDetails(retrieveOutOfStock);
  }
}
