package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.AddBookUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.GetBookUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.api.BookApi;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BooksController implements BookApi {

  private final AddBookUseCase addBookUseCase;

  private final GetBookUseCase getBookUseCase;

  private final BookRestMapper bookRestMapper;

  @Override
  public ResponseEntity<BookDto> addBook(final BookRequestDto bookRequestDto) {
    final Book bookToSave = this.bookRestMapper.toEntity(bookRequestDto);
    final Book bookSaved = this.addBookUseCase.addBook(bookToSave);
    final BookDto bookDto = this.bookRestMapper.toDto(bookSaved);
    return ResponseEntity.status(HttpStatus.CREATED).body(bookDto);
  }

  @Override
  public ResponseEntity<Void> deleteBook(final Long bookId) {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  public ResponseEntity<List<BookDto>> getAllBooks() {
    throw new NotImplementedException("Not implemented yet");
  }

  @Override
  public ResponseEntity<BookDto> getBookById(final Long bookId) {
    final Book bookFound = this.getBookUseCase.getBookDetails(bookId);
    final BookDto bookDto = this.bookRestMapper.toDto(bookFound);
    return ResponseEntity.ok(bookDto);
  }

  @Override
  public ResponseEntity<BookDto> updateBook(
      final Long bookId, final BookRequestDto bookRequestDto) {
    throw new NotImplementedException("Not implemented yet");
  }
}
