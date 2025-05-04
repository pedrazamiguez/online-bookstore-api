package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.BooksController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.book.base.BaseBooksTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.GetBookUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(SpringExtension.class)
class GetBookByIdTest extends BaseBooksTestController {

  private static final String GET_BOOK_BY_ID_ENDPOINT_PATH = "/v1/books/{bookId}";

  @InjectMocks private BooksController booksController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private GetBookUseCase getBookUseCase;

  @Mock private BookRestMapper bookRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.booksController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "GET " + GET_BOOK_BY_ID_ENDPOINT_PATH + " returns HTTP 200 with BookDto when book is found")
  void shouldReturn200AndBookDto_whenBookFound() throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final Book bookFound = this.givenBook(bookId);
    final BookDto bookDto = this.givenBookDto(bookId);

    when(this.getBookUseCase.getBookDetails(bookId)).thenReturn(bookFound);
    when(this.bookRestMapper.toDto(bookFound)).thenReturn(bookDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                get(GET_BOOK_BY_ID_ENDPOINT_PATH, bookId).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(bookDto));
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.getBookUseCase).getBookDetails(bookId);
    verify(this.bookRestMapper).toDto(bookFound);
    verifyNoMoreInteractions(this.getBookUseCase, this.bookRestMapper);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "GET " + GET_BOOK_BY_ID_ENDPOINT_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final NullPointerException exception = new NullPointerException("Unexpected error");

    when(this.getBookUseCase.getBookDetails(bookId)).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                get(GET_BOOK_BY_ID_ENDPOINT_PATH, bookId).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.getBookUseCase).getBookDetails(bookId);
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoInteractions(this.bookRestMapper);
  }

  @Nested
  @DisplayName("GET " + GET_BOOK_BY_ID_ENDPOINT_PATH + " returns HTTP 4xx on client cases")
  class ClientCases {

    @Test
    @DisplayName("GET " + GET_BOOK_BY_ID_ENDPOINT_PATH + " returns HTTP 400 when bookId is invalid")
    void shouldReturn400_whenBookIdInvalid() throws Exception {
      // GIVEN
      final String invalidBookId = "not-a-number";

      // WHEN
      final MvcResult result =
          GetBookByIdTest.this
              .mockMvc
              .perform(
                  get(GET_BOOK_BY_ID_ENDPOINT_PATH, invalidBookId)
                      .contentType(MediaType.APPLICATION_JSON))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(GetBookByIdTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.BAD_REQUEST),
              any(MethodArgumentTypeMismatchException.class),
              any(WebRequest.class));

      verifyNoInteractions(
          GetBookByIdTest.this.getBookUseCase, GetBookByIdTest.this.bookRestMapper);
    }
  }
}
