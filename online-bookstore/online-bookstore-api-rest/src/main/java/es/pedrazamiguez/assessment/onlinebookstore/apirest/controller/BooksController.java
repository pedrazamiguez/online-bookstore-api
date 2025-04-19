package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.AddBookUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.GetBookDetailsUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.BookApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BooksController implements BookApi {

  private final AddBookUseCase addBookUseCase;

  private final GetBookDetailsUseCase getBookDetailsUseCase;

  private final BookRestMapper bookRestMapper;

  @Override
  public ResponseEntity<BookDto> addBook(final BookRequestDto bookRequestDto) {
    final Book bookToSave = this.bookRestMapper.toEntity(bookRequestDto);
    final Book bookSaved = this.addBookUseCase.addBook(bookToSave);
    final BookDto bookDto = this.bookRestMapper.toDto(bookSaved);
    return ResponseEntity.status(HttpStatus.CREATED).body(bookDto);
  }

  @Override
  public ResponseEntity<BookDto> getBookById(final Long bookId) {
    final Book bookFound = this.getBookDetailsUseCase.getBookDetails(bookId);
    final BookDto bookDto = this.bookRestMapper.toDto(bookFound);
    return ResponseEntity.ok(bookDto);
  }
}
