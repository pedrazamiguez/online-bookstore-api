package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

public abstract class ErrorRestMapperDecorator implements ErrorRestMapper {

  protected ErrorRestMapper delegate;

  public void setDelegate(final ErrorRestMapper delegate) {
    this.delegate = delegate;
  }

  @Override
  public ErrorDto toDto(final HttpStatus status, final Exception e, final WebRequest request) {
    return delegate.toDto(status, e.getMessage(), request);
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

    return delegate.toDto(status, message, request);
  }
}
