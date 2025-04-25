package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.BookCopyStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookCopyEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.projection.InventoryDetailsQueryResult;
import java.util.List;
import java.util.stream.IntStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookCopyEntityMapper {

  default List<BookCopyEntity> createBookCopies(final BookEntity bookEntity, final Long copies) {
    return IntStream.range(0, copies.intValue())
        .mapToObj(
            i -> {
              final BookCopyEntity bookCopyEntity = new BookCopyEntity();
              bookCopyEntity.setBook(bookEntity);
              this.patchWithStatus(bookCopyEntity, BookCopyStatus.AVAILABLE);
              return bookCopyEntity;
            })
        .toList();
  }

  default void patchWithStatus(
      final BookCopyEntity bookCopyEntity, final BookCopyStatus status) {
    bookCopyEntity.setStatus(status);
  }

  default void patchWithStatus(
      final List<BookCopyEntity> bookCopyEntities, final BookCopyStatus status) {
    bookCopyEntities.forEach(bookCopyEntity -> this.patchWithStatus(bookCopyEntity, status));
  }

  List<BookAllocation> toDomainList(
      List<InventoryDetailsQueryResult> inventoryDetailsQueryResultList);

  @Mapping(target = "book.id", source = "bookId")
  @Mapping(target = "book.isbn", source = "isbn")
  @Mapping(target = "book.title", source = "title")
  @Mapping(target = "book.author", source = "author")
  @Mapping(target = "book.publisher", source = "publisher")
  @Mapping(target = "book.yearPublished", source = "yearPublished")
  @Mapping(target = "book.price", source = "price")
  @Mapping(target = "book.genre", source = "genre")
  @Mapping(target = "book.type.code", source = "typeCode")
  BookAllocation inventoryDetailsDtoToInventoryDetails(
      InventoryDetailsQueryResult inventoryDetailsQueryResult);
}
