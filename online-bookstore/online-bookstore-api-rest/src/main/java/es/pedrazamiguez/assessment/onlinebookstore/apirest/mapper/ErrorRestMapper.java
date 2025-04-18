package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ErrorRestMapper {

    @Mapping(target = "code", expression = "java( String.valueOf(status.value()) )")
    @Mapping(target = "message", source = "e.message")
    ErrorDto toDto(HttpStatus status, Exception e, WebRequest request);
}
