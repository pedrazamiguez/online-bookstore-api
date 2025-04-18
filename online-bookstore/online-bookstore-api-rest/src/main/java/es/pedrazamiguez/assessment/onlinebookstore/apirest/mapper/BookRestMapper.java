package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import org.mapstruct.Mapper;

@Mapper
public interface BookRestMapper {

  Book toEntity(BookDto bookDto);

  BookDto toDto(Book book);
}
