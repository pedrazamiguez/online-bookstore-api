package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.InventoryDetails;
import java.util.List;

public interface BookCopyRepository {

  void addCopies(Long bookId, int copies);

  List<InventoryDetails> getInventoryDetails(boolean retrieveOutOfStock);
}
