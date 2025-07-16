package es.pedrazamiguez.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.onlinebookstore.openapi.model.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
@DecoratedWith(ErrorRestMapperDecorator.class)
public interface ErrorRestMapper {

  @Mapping(target = "status", expression = "java( status.name() )")
  @Mapping(target = "timestamp", expression = "java( java.time.LocalDateTime.now() )")
  ErrorDto toDto(HttpStatus status, String message, String path);

  ErrorDto toDto(HttpStatus status, Exception e, WebRequest request);

  ErrorDto toDto(HttpStatus status, MethodArgumentNotValidException e, WebRequest request);

  ErrorDto toDto(HttpStatus status, ConstraintViolationException e, WebRequest request);

  ErrorDto toDto(HttpStatus status, MethodArgumentTypeMismatchException e, WebRequest request);

  ErrorDto toDto(HttpStatus status, HttpMessageNotReadableException e, WebRequest request);
}
