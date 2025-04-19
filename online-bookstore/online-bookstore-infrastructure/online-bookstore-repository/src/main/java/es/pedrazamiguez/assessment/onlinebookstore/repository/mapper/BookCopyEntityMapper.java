package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookCopyEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.mapstruct.Mapper;
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
}
