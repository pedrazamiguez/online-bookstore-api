package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory;

@FunctionalInterface
public interface AddToInventoryUseCase {

  void addToInventory(Long bookId, int copies);
}
