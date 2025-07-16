package es.pedrazamiguez.api.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.api.onlinebookstore.domain.model.BookAllocation;
import java.util.Optional;

@FunctionalInterface
public interface AddToInventoryUseCase {

  Optional<BookAllocation> addToInventory(Long bookId, Long copies);
}
