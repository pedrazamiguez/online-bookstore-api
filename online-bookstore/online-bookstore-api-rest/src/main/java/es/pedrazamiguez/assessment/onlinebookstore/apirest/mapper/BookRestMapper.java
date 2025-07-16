package es.pedrazamiguez.api.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.api.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.api.onlinebookstore.openapi.model.BookRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookRestMapper {

  @Mapping(target = "yearPublished", source = "year")
  @Mapping(target = "type.code", source = "type")
  Book toEntity(BookDto bookDto);

  @Mapping(target = "year", source = "yearPublished")
  @Mapping(target = "type", source = "type.code")
  @Mapping(target = "price", source = "price")
  BookDto toDto(Book book);

  @Mapping(target = "yearPublished", source = "year")
  @Mapping(target = "type.code", source = "type")
  Book toEntity(BookRequestDto bookRequestDto);
}
