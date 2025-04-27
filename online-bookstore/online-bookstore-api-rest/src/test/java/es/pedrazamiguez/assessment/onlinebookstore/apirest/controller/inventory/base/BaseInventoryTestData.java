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
        return Instancio.of(AllocationDto.class)
                .supply(field(AllocationDto::getCopies), gen -> copies)
                .create();
    }

    protected Book givenBook(final Long bookId) {
        final Book book = Instancio.create(Book.class);
        book.setId(bookId);
        return book;
    }

    protected BookAllocation givenBookAllocation(final Long bookId, final Long copies) {
        return Instancio.of(BookAllocation.class)
                .supply(field(BookAllocation::getBook), gen -> this.givenBook(bookId))
                .supply(field(BookAllocation::getCopies), gen -> copies)
                .create();
    }

    protected InventoryItemDto givenInventoryItemDto(final Long bookId, final Long copies) {
        return Instancio.of(InventoryItemDto.class)
                .supply(field(InventoryItemDto::getBookId), gen -> bookId)
                .supply(field(InventoryItemDto::getCopies), gen -> copies)
                .create();
    }
}
