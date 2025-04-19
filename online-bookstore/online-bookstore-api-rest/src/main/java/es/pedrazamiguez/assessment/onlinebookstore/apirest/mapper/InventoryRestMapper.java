package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.InventoryDetails;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.InventoryItemDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryRestMapper {

  List<InventoryItemDto> toDtoList(List<InventoryDetails> inventoryDetailsList);

  @Mapping(target = "bookId", source = "book.id")
  @Mapping(target = "isbn", source = "book.isbn")
  @Mapping(target = "title", source = "book.title")
  @Mapping(target = "author", source = "book.author")
  @Mapping(target = "publisher", source = "book.publisher")
  @Mapping(target = "year", source = "book.yearPublished")
  @Mapping(target = "price", source = "book.price")
  @Mapping(target = "genre", source = "book.genre")
  @Mapping(target = "type", source = "book.type.code")
  InventoryItemDto inventoryDetailsToInventoryItemDto(InventoryDetails inventoryDetails);
}
