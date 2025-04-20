package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;

import java.util.Optional;

@FunctionalInterface
public interface AddToInventoryUseCase {

  Optional<BookAllocation> addToInventory(Long bookId, Long copies);
}
