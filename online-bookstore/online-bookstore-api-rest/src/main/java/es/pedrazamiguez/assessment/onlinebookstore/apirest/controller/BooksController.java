package es.pedrazamiguez.api.onlinebookstore.apirest.controller;

import es.pedrazamiguez.api.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.api.onlinebookstore.domain.usecase.book.AddBookUseCase;
import es.pedrazamiguez.api.onlinebookstore.domain.usecase.book.GetBookUseCase;
import es.pedrazamiguez.api.onlinebookstore.openapi.api.BookApi;
import es.pedrazamiguez.api.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.api.onlinebookstore.openapi.model.BookRequestDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BooksController implements BookApi {

  private final AddBookUseCase addBookUseCase;

  private final GetBookUseCase getBookUseCase;

  private final BookRestMapper bookRestMapper;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BookDto> addBook(final BookRequestDto bookRequestDto) {
    final Book bookToSave = this.bookRestMapper.toEntity(bookRequestDto);
    final Book bookSaved = this.addBookUseCase.addBook(bookToSave);
    final BookDto bookDto = this.bookRestMapper.toDto(bookSaved);
    return ResponseEntity.status(HttpStatus.CREATED).body(bookDto);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteBook(final Long bookId) {
    throw new NotImplementedException("Delete book not implemented yet");
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<List<BookDto>> getAllBooks() {
    throw new NotImplementedException("Get all books not implemented yet");
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BookDto> getBookById(final Long bookId) {
    final Book bookFound = this.getBookUseCase.getBookDetails(bookId);
    final BookDto bookDto = this.bookRestMapper.toDto(bookFound);
    return ResponseEntity.ok(bookDto);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BookDto> updateBook(
      final Long bookId, final BookRequestDto bookRequestDto) {
    throw new NotImplementedException("Update book not implemented yet");
  }
}
