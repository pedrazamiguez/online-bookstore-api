package es.pedrazamiguez.api.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.api.onlinebookstore.domain.model.BookAllocation;
import java.util.List;

@FunctionalInterface
public interface GetInventoryStatusUseCase {

  List<BookAllocation> getInventoryStatus(boolean includeOutOfStock);
}
