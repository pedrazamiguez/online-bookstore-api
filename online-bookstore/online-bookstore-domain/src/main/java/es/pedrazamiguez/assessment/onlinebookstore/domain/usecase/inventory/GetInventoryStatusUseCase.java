package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import java.util.List;

@FunctionalInterface
public interface GetInventoryStatusUseCase {

  List<BookAllocation> getInventoryStatus(boolean includeOutOfStock);
}
