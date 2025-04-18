package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.BookApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BooksController implements BookApi {
    @Override
    public ResponseEntity<BookDto> addBook(BookRequestDto bookRequestDto) {
        throw new NotImplementedException("Add book not implemented yet");
    }

    @Override
    public ResponseEntity<BookDto> getBookById(Long bookId) {
        throw new NotImplementedException("Get book by ID not implemented yet");
    }
}
