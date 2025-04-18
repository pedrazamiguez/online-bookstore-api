package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
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
    final Book bookFound = this.getBookDetailsUseCase.getBookDetails(bookId);
    final BookDto bookDto = this.bookRestMapper.toDto(bookFound);
    return ResponseEntity.ok(bookDto);
  }
}
