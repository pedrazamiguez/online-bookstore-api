package es.pedrazamiguez.assessment.onlinebookstore.apirest.exception;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class BookstoreExceptionHandler {

    private final ErrorRestMapper errorRestMapper;

    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<ErrorDto> handleNotImplementedException(final NotImplementedException e, final WebRequest request) {
        final HttpStatus status = HttpStatus.NOT_IMPLEMENTED;
        return ResponseEntity.status(status).body(this.errorRestMapper.toDto(status, e, request));
    }
}
