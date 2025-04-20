package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;

import java.util.List;

public interface GetInventoryStatusUseCase {

  List<BookAllocation> getInventoryStatus(boolean includeOutOfStock);
}
