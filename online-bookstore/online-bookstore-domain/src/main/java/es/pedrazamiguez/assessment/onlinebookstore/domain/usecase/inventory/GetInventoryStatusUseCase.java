package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.InventoryDetails;
import java.util.List;

public interface GetInventoryStatusUseCase {

  List<InventoryDetails> getInventoryStatus(boolean retrieveOutOfStock);
}
