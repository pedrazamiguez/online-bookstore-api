package es.pedrazamiguez.onlinebookstore.domain.usecase.inventory;

import java.time.LocalDateTime;

@FunctionalInterface
public interface DeleteOldCopiesFromInventoryUseCase {

  void deleteOldCopies(LocalDateTime olderThan);
}
