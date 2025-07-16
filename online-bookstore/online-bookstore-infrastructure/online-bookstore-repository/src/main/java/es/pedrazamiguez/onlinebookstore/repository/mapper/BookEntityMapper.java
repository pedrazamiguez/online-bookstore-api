package es.pedrazamiguez.onlinebookstore.repository.mapper;

import es.pedrazamiguez.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.onlinebookstore.repository.entity.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookEntityMapper {

  Book toDomain(BookEntity bookEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  BookEntity toEntity(Book book);
}
