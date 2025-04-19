package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookRestMapper {

  Book toEntity(BookDto bookDto);

  BookDto toDto(Book book);

  @Mapping(target = "yearPublished", source = "year")
  @Mapping(target = "type.code", source = "type")
  Book toEntity(BookRequestDto bookRequestDto);
}
