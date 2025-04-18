package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.GetBookDetailsUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.BookApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BooksController implements BookApi {

  private final GetBookDetailsUseCase getBookDetailsUseCase;

  private final BookRestMapper bookRestMapper;

  @Override
  public ResponseEntity<BookDto> addBook(BookRequestDto bookRequestDto) {
    throw new NotImplementedException("Add book not implemented yet");
  }

  @Override
  public ResponseEntity<BookDto> getBookById(Long bookId) {
    final var bookFound = this.getBookDetailsUseCase.getBookDetails(bookId);
    return bookFound
        .map(this.bookRestMapper::toDto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
