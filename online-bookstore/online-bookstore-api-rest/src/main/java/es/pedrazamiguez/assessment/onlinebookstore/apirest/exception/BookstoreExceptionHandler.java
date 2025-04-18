package es.pedrazamiguez.assessment.onlinebookstore.apirest.exception;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.ErrorDto;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class BookstoreExceptionHandler {

    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<ErrorDto> handleNotImplementedException(final NotImplementedException e, final WebRequest request) {
        final HttpStatus status = HttpStatus.NOT_IMPLEMENTED;
        return ResponseEntity.status(status)
                .body(new ErrorDto()
                        .code(String.valueOf(status.value()))
                        .message(e.getMessage())
//                        .path(request.getDescription(false))
                );
    }
}
