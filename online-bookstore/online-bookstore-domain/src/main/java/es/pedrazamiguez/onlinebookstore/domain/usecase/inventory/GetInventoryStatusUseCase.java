package es.pedrazamiguez.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import java.util.List;

@FunctionalInterface
public interface GetInventoryStatusUseCase {

  List<BookAllocation> getInventoryStatus(boolean includeOutOfStock);
}
