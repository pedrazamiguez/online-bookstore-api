package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

public abstract class ErrorRestMapperDecorator implements ErrorRestMapper {

  @Autowired protected ErrorRestMapper delegate;

  @Override
  public ErrorDto toDto(final HttpStatus status, final Exception e, final WebRequest request) {
    return this.delegate.toDto(status, e.getMessage(), this.extractPath(request));
  }

  @Override
  public ErrorDto toDto(
      final HttpStatus status, final MethodArgumentNotValidException e, final WebRequest request) {
    String message =
        e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));

    if (StringUtils.isEmpty(message)) {
      message = "Validation error";
    }

    return this.delegate.toDto(status, message, this.extractPath(request));
  }

  @Override
  public ErrorDto toDto(
      final HttpStatus status, final ConstraintViolationException e, final WebRequest request) {
    String message =
        e.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));

    if (StringUtils.isEmpty(message)) {
      message = "Validation error";
    }

    return this.delegate.toDto(status, message, this.extractPath(request));
  }

  @Override
  public ErrorDto toDto(
      HttpStatus status, MethodArgumentTypeMismatchException e, WebRequest request) {
    final String typeName =
        ObjectUtils.isEmpty(e.getRequiredType()) ? "Unknown" : e.getRequiredType().getSimpleName();
    String message =
        String.format(
            "The parameter '%s' of value '%s' could not be converted to type '%s'",
            e.getName(), e.getValue(), typeName);

    return this.delegate.toDto(status, message, this.extractPath(request));
  }

  private String extractPath(WebRequest request) {
    String desc = request.getDescription(false);
    return desc.startsWith("uri=") ? desc.substring(4) : desc;
  }
}
