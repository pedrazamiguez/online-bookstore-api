package es.pedrazamiguez.assessment.onlinebookstore.apirest.exception;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

  private final ErrorRestMapper errorRestMapper;

  @ExceptionHandler(NotImplementedException.class)
  public ResponseEntity<ErrorDto> handleNotImplementedException(
      final NotImplementedException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.NOT_IMPLEMENTED;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorDto> handleConstraintViolationException(
      final ConstraintViolationException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorDto> handleMethodArgumentTypeMismatchException(
      final MethodArgumentTypeMismatchException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorDto> handleMissingServletRequestParameterException(
      final MissingServletRequestParameterException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ErrorDto> handleAuthorizationDeniedException(
      final AuthorizationDeniedException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.FORBIDDEN;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorDto> handleNoResourceFoundException(
      final NoResourceFoundException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorDto> handleHttpRequestMethodNotSupportedException(
      final HttpRequestMethodNotSupportedException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorDto> handleNotReadableException(
      final HttpMessageNotReadableException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDto> handleException(final Exception e, final WebRequest request) {
    log.error("Unexpected error: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
    final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }
}
