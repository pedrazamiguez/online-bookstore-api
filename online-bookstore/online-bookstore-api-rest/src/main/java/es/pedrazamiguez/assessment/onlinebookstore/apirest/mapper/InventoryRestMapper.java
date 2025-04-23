package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.InventoryItemDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryRestMapper {

  List<InventoryItemDto> toDtoList(List<BookAllocation> bookAllocationList);

  @Mapping(target = "bookId", source = "book.id")
  @Mapping(target = "isbn", source = "book.isbn")
  @Mapping(target = "title", source = "book.title")
  @Mapping(target = "author", source = "book.author")
  @Mapping(target = "publisher", source = "book.publisher")
  @Mapping(target = "year", source = "book.yearPublished")
  @Mapping(target = "price", source = "book.price", qualifiedByName = "withPrecision")
  @Mapping(target = "genre", source = "book.genre")
  @Mapping(target = "type", source = "book.type.code")
  InventoryItemDto inventoryDetailsToInventoryItemDto(BookAllocation bookAllocation);

  @Named("withPrecision")
  default BigDecimal withPrecision(final BigDecimal value) {
    if (value == null) {
      return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    return value.setScale(2, RoundingMode.HALF_UP);
  }
}
