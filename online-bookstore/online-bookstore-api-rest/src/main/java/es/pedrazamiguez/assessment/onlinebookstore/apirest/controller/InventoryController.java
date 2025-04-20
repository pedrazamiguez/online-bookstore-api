package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.InventoryRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.AddToInventoryUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.GetInventoryStatusUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.InventoryApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.InventoryItemDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.InventoryRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InventoryController implements InventoryApi {

  private final AddToInventoryUseCase addToInventoryUseCase;

  private final GetInventoryStatusUseCase getInventoryStatusUseCase;

  private final InventoryRestMapper inventoryRestMapper;

  @Override
  public ResponseEntity<Void> addBookCopiesToInventory(
      final InventoryRequestDto inventoryRequestDto) {
    this.addToInventoryUseCase.addToInventory(
        inventoryRequestDto.getBookId(), inventoryRequestDto.getCopies());
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<InventoryItemDto>> getInventory(final Boolean includeOutOfStock) {
    final List<BookAllocation> bookAllocationFound =
        this.getInventoryStatusUseCase.getInventoryStatus(Boolean.TRUE.equals(includeOutOfStock));
    final List<InventoryItemDto> inventoryItemDtoList =
        this.inventoryRestMapper.toDtoList(bookAllocationFound);
    return ResponseEntity.ok(inventoryItemDtoList);
  }
}
