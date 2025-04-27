package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookRestMapper {

    @Mapping(target = "yearPublished", source = "year")
    @Mapping(target = "type.code", source = "type")
    Book toEntity(BookDto bookDto);

    @Mapping(target = "year", source = "yearPublished")
    @Mapping(target = "type", source = "type.code")
    @Mapping(target = "price", source = "price", qualifiedByName = "withPrecision")
    BookDto toDto(Book book);

    @Mapping(target = "yearPublished", source = "year")
    @Mapping(target = "type.code", source = "type")
    Book toEntity(BookRequestDto bookRequestDto);

    @Named("withPrecision")
    default BigDecimal withPrecision(final BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
