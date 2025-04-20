package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.repository.dto.InventoryDetailsDto;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookCopyEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookCopyEntityMapper {

  default List<BookCopyEntity> toEntityList(final BookEntity bookEntity, final int copies) {
    return IntStream.range(0, copies)
        .mapToObj(
            i -> {
              final BookCopyEntity bookCopyEntity = new BookCopyEntity();
              bookCopyEntity.setBook(bookEntity);
              return bookCopyEntity;
            })
        .collect(Collectors.toList());
  }

  List<BookAllocation> toDomainList(List<InventoryDetailsDto> inventoryDetailsDtoList);

  @Mapping(target = "book.id", source = "bookId")
  @Mapping(target = "book.isbn", source = "isbn")
  @Mapping(target = "book.title", source = "title")
  @Mapping(target = "book.author", source = "author")
  @Mapping(target = "book.publisher", source = "publisher")
  @Mapping(target = "book.yearPublished", source = "yearPublished")
  @Mapping(target = "book.price", source = "price")
  @Mapping(target = "book.genre", source = "genre")
  @Mapping(target = "book.type.code", source = "typeCode")
  BookAllocation inventoryDetailsDtoToInventoryDetails(InventoryDetailsDto inventoryDetailsDto);
}
