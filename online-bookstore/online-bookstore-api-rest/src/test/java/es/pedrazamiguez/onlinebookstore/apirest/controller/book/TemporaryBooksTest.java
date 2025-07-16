package es.pedrazamiguez.onlinebookstore.apirest.controller.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import es.pedrazamiguez.onlinebookstore.apirest.controller.BooksController;
import es.pedrazamiguez.onlinebookstore.apirest.controller.book.base.BaseBooksTestController;
import es.pedrazamiguez.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.onlinebookstore.domain.usecase.book.AddBookUseCase;
import es.pedrazamiguez.onlinebookstore.domain.usecase.book.GetBookUseCase;
import es.pedrazamiguez.onlinebookstore.openapi.model.BookRequestDto;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(SpringExtension.class)
class TemporaryBooksTest extends BaseBooksTestController {

  private static final String BOOKS_ENDPOINT_PATH = "/v1/books";
  private static final String BOOK_BY_ID_ENDPOINT_PATH = "/v1/books/{bookId}";

  @InjectMocks private BooksController booksController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private AddBookUseCase addBookUseCase;

  @Mock private GetBookUseCase getBookUseCase;

  @Mock private BookRestMapper bookRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.booksController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName("DELETE " + BOOK_BY_ID_ENDPOINT_PATH + " returns HTTP 501 when not implemented")
  void shouldReturn501_whenDeleteBookNotImplemented() throws Exception {
    // GIVEN
    final Long bookId = 1L;

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(BOOK_BY_ID_ENDPOINT_PATH, bookId).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_IMPLEMENTED.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.errorRestMapper)
        .toDto(
            eq(HttpStatus.NOT_IMPLEMENTED),
            any(NotImplementedException.class),
            any(WebRequest.class));
    verifyNoInteractions(this.addBookUseCase, this.getBookUseCase, this.bookRestMapper);
    verifyNoMoreInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName("GET " + BOOKS_ENDPOINT_PATH + " returns HTTP 501 when not implemented")
  void shouldReturn501_whenGetAllBooksNotImplemented() throws Exception {
    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(BOOKS_ENDPOINT_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_IMPLEMENTED.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.errorRestMapper)
        .toDto(
            eq(HttpStatus.NOT_IMPLEMENTED),
            any(NotImplementedException.class),
            any(WebRequest.class));
    verifyNoInteractions(this.addBookUseCase, this.getBookUseCase, this.bookRestMapper);
    verifyNoMoreInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName("PUT " + BOOK_BY_ID_ENDPOINT_PATH + " returns HTTP 501 when not implemented")
  void shouldReturn501_whenUpdateBookNotImplemented() throws Exception {
    // GIVEN
    final Long bookId = 1L;
    final BookRequestDto bookRequestDto = this.givenBookRequestDto();

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                put(BOOK_BY_ID_ENDPOINT_PATH, bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(bookRequestDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_IMPLEMENTED.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.errorRestMapper)
        .toDto(
            eq(HttpStatus.NOT_IMPLEMENTED),
            any(NotImplementedException.class),
            any(WebRequest.class));
    verifyNoInteractions(this.addBookUseCase, this.getBookUseCase, this.bookRestMapper);
    verifyNoMoreInteractions(this.errorRestMapper);
  }
}
