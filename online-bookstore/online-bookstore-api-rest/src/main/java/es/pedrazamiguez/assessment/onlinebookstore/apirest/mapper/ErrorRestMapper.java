package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ErrorRestMapper {

    @Mapping(target = "status", expression = "java( status.name() )")
    @Mapping(target = "path", expression = "java( request.getDescription(false) )")
    @Mapping(target = "timestamp", expression = "java( java.time.LocalDateTime.now() )")
    ErrorDto toDto(HttpStatus status, String message, WebRequest request);

    default ErrorDto toDto(HttpStatus status, Exception e, WebRequest request) {
        return toDto(status, e.getMessage(), request);
    }

    default ErrorDto toDto(HttpStatus status, MethodArgumentNotValidException e, WebRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                          .collect(Collectors.joining(", "));
        if (StringUtils.isEmpty(message)) {
            message = "Validation error";
        }
        return toDto(status, message, request);
    }
}
