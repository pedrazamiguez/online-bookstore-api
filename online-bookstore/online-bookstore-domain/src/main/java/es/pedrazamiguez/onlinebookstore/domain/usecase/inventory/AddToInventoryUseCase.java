package es.pedrazamiguez.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import java.util.Optional;

@FunctionalInterface
public interface AddToInventoryUseCase {

  Optional<BookAllocation> addToInventory(Long bookId, Long copies);
}
