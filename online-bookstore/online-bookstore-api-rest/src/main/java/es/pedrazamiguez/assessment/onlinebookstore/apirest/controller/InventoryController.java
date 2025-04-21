package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.InventoryRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.AddToInventoryUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.DeleteFromInventoryUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.DeleteOldCopiesFromInventoryUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.GetInventoryStatusUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.InventoryApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.InventoryItemDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InventoryController implements InventoryApi {

  private final AddToInventoryUseCase addToInventoryUseCase;

  private final DeleteFromInventoryUseCase deleteFromInventoryUseCase;

  private final DeleteOldCopiesFromInventoryUseCase deleteOldCopiesFromInventoryUseCase;

  private final GetInventoryStatusUseCase getInventoryStatusUseCase;

  private final InventoryRestMapper inventoryRestMapper;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<InventoryItemDto> addBookCopiesToInventory(
      final Long bookId, final AllocationDto allocationDto) {

    final Optional<BookAllocation> bookAllocation =
        this.addToInventoryUseCase.addToInventory(bookId, allocationDto.getCopies());

    return bookAllocation
        .map(this.inventoryRestMapper::inventoryDetailsToInventoryItemDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<InventoryItemDto>> getInventory(final Boolean includeOutOfStock) {
    final List<BookAllocation> bookAllocationFound =
        this.getInventoryStatusUseCase.getInventoryStatus(Boolean.TRUE.equals(includeOutOfStock));
    final List<InventoryItemDto> inventoryItemDtoList =
        this.inventoryRestMapper.toDtoList(bookAllocationFound);
    return ResponseEntity.ok(inventoryItemDtoList);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<InventoryItemDto> removeBookCopiesFromInventory(
      final Long bookId, final Long copies) {

    final Optional<BookAllocation> bookAllocation =
        this.deleteFromInventoryUseCase.deleteFromInventory(bookId, copies);

    return bookAllocation
        .map(this.inventoryRestMapper::inventoryDetailsToInventoryItemDto)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> removeOldBookCopies(final LocalDateTime date) {
    this.deleteOldCopiesFromInventoryUseCase.deleteOldCopies(date);
    return ResponseEntity.noContent().build();
  }
}
