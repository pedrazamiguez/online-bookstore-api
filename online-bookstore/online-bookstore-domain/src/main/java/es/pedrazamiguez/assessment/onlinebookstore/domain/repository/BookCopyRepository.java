package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import java.util.List;
import java.util.Optional;

public interface BookCopyRepository {

  void addCopies(Long bookId, int copies);

  List<BookAllocation> getInventoryDetails(boolean includeOutOfStock);

  Optional<BookAllocation> getInventoryDetailsByBookId(Long bookId);
}
