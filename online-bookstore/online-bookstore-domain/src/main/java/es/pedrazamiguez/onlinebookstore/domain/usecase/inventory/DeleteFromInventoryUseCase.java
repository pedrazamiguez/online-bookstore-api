package es.pedrazamiguez.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import java.util.Optional;

@FunctionalInterface
public interface DeleteFromInventoryUseCase {

  Optional<BookAllocation> deleteFromInventory(Long bookId, Long copies);
}
