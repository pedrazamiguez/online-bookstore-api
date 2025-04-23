package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookCopyRepository {

  void addCopies(Long bookId, Long copies);

  void deleteCopies(Long bookId, Long copies);

  void deleteCopiesOlderThan(LocalDateTime olderThan);

  List<BookAllocation> getInventoryDetails(boolean includeOutOfStock);

  Optional<BookAllocation> getInventoryDetailsByBookId(Long bookId);
}
