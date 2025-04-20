package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;

import java.util.List;

public interface BookCopyRepository {

  void addCopies(Long bookId, int copies);

  List<BookAllocation> getInventoryDetails(boolean includeOutOfStock);
}
