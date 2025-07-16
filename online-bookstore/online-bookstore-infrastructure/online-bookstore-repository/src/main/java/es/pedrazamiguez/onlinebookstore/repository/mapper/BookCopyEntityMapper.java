package es.pedrazamiguez.onlinebookstore.repository.mapper;

import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.onlinebookstore.repository.projection.InventoryDetailsQueryResult;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookCopyEntityMapper {

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

  List<BookAllocation> toDomainList(
      List<InventoryDetailsQueryResult> inventoryDetailsQueryResultList);
}
