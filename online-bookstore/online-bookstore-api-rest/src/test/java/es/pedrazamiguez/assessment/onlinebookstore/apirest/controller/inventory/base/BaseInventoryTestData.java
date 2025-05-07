package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.inventory.base;

import static org.instancio.Select.all;
import static org.instancio.Select.field;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.InventoryItemDto;
import org.instancio.Instancio;

public abstract class BaseInventoryTestData {

  protected Long createRandomCopies() {
    return Instancio.of(Long.class)
        .supply(all(Long.class), gen -> gen.longRange(1L, 1000L))
        .create();
  }

  protected AllocationDto givenAllocationDto(final Long copies) {
    return Instancio.of(AllocationDto.class).set(field(AllocationDto::getCopies), copies).create();
  }

  protected Book givenBook(final Long bookId) {
    final Book book = Instancio.create(Book.class);
    book.setId(bookId);
    return book;
  }

  protected BookAllocation givenBookAllocation(final Long bookId, final Long copies) {
    return Instancio.of(BookAllocation.class)
        .set(field(BookAllocation::getBook), this.givenBook(bookId))
        .set(field(BookAllocation::getCopies), copies)
        .create();
  }

  protected InventoryItemDto givenInventoryItemDto(final Long bookId, final Long copies) {
    return Instancio.of(InventoryItemDto.class)
        .set(field(InventoryItemDto::getBookId), bookId)
        .set(field(InventoryItemDto::getCopies), copies)
        .create();
  }
}
