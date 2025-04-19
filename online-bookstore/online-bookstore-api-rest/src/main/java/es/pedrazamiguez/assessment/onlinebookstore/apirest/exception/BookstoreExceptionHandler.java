package es.pedrazamiguez.assessment.onlinebookstore.apirest.exception;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookAlreadyExistsException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class BookstoreExceptionHandler {

  private final ErrorRestMapper errorRestMapper;

  @ExceptionHandler(BookNotFoundException.class)
  public ResponseEntity<ErrorDto> handleBookNotFoundException(
      final BookNotFoundException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }

  @ExceptionHandler(BookAlreadyExistsException.class)
  public ResponseEntity<ErrorDto> handleBookAlreadyExistsException(
      final BookAlreadyExistsException e, final WebRequest request) {
    final HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
  }
}
