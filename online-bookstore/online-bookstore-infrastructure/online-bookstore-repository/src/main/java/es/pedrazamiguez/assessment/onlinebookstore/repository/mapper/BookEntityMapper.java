package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookEntityMapper {

  Book toDomain(BookEntity bookEntity);

  BookEntity toEntity(Book book);
}
