package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.BooksController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.book.base.BaseBooksTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.BookRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookAlreadyExistsException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.book.AddBookUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(SpringExtension.class)
class AddBookTest extends BaseBooksTestController {

  private static final String ADD_BOOK_ENDPOINT_PATH = "/v1/books";

  @InjectMocks private BooksController booksController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private AddBookUseCase addBookUseCase;

  @Mock private BookRestMapper bookRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.booksController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "POST " + ADD_BOOK_ENDPOINT_PATH + " returns HTTP 201 with BookDto when book is added")
  void shouldReturn201AndBookDto_whenBookAdded() throws Exception {
    // GIVEN
    final BookRequestDto bookRequestDto = this.givenBookRequestDto();
    final Book bookToSave = this.givenBook();
    final Book bookSaved = this.givenBook(bookToSave.getId());
    final BookDto bookDto = this.givenBookDto(bookToSave.getId());

    when(this.bookRestMapper.toEntity(bookRequestDto)).thenReturn(bookToSave);
    when(this.addBookUseCase.addBook(bookToSave)).thenReturn(bookSaved);
    when(this.bookRestMapper.toDto(bookSaved)).thenReturn(bookDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                post(ADD_BOOK_ENDPOINT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(bookRequestDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(bookDto));
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.bookRestMapper).toEntity(bookRequestDto);
    verify(this.addBookUseCase).addBook(bookToSave);
    verify(this.bookRestMapper).toDto(bookSaved);
    verifyNoMoreInteractions(this.bookRestMapper, this.addBookUseCase);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName("POST " + ADD_BOOK_ENDPOINT_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final BookRequestDto bookRequestDto = this.givenBookRequestDto();
    final Book bookToSave = this.givenBook();
    final NullPointerException exception = new NullPointerException("Unexpected error");

    when(this.bookRestMapper.toEntity(bookRequestDto)).thenReturn(bookToSave);
    when(this.addBookUseCase.addBook(bookToSave)).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                post(ADD_BOOK_ENDPOINT_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(bookRequestDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.bookRestMapper).toEntity(bookRequestDto);
    verify(this.addBookUseCase).addBook(bookToSave);
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoMoreInteractions(this.bookRestMapper, this.addBookUseCase);
  }

  @Nested
  @DisplayName("POST " + ADD_BOOK_ENDPOINT_PATH + " returns HTTP 4xx on client cases")
  class ClientCases {

    @Test
    @DisplayName(
        "POST " + ADD_BOOK_ENDPOINT_PATH + " returns HTTP 400 when book structure is malformed")
    void shouldReturn400_whenBookStructureMalformed() throws Exception {
      // GIVEN
      final BookRequestDto bookRequestDto = new BookRequestDto(); // Missing required fields

      // WHEN
      final MvcResult result =
          AddBookTest.this
              .mockMvc
              .perform(
                  post(ADD_BOOK_ENDPOINT_PATH)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(AddBookTest.this.objectMapper.writeValueAsString(bookRequestDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(AddBookTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.BAD_REQUEST),
              any(MethodArgumentNotValidException.class),
              any(WebRequest.class));
      verifyNoInteractions(AddBookTest.this.bookRestMapper, AddBookTest.this.addBookUseCase);
    }

    @Test
    @DisplayName("POST " + ADD_BOOK_ENDPOINT_PATH + " returns HTTP 409 when book already exists")
    void shouldReturn409_whenBookAlreadyExists() throws Exception {
      // GIVEN
      final BookRequestDto bookRequestDto = AddBookTest.this.givenBookRequestDto();
      final Book bookToSave = AddBookTest.this.givenBook();
      final RuntimeException exception = new BookAlreadyExistsException(bookToSave.getIsbn());

      when(AddBookTest.this.bookRestMapper.toEntity(bookRequestDto)).thenReturn(bookToSave);
      when(AddBookTest.this.addBookUseCase.addBook(bookToSave)).thenThrow(exception);

      // WHEN
      final MvcResult result =
          AddBookTest.this
              .mockMvc
              .perform(
                  post(ADD_BOOK_ENDPOINT_PATH)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(AddBookTest.this.objectMapper.writeValueAsString(bookRequestDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(AddBookTest.this.bookRestMapper).toEntity(bookRequestDto);
      verify(AddBookTest.this.addBookUseCase).addBook(bookToSave);
      verify(AddBookTest.this.errorRestMapper)
          .toDto(eq(HttpStatus.CONFLICT), eq(exception), any(WebRequest.class));
      verifyNoMoreInteractions(AddBookTest.this.bookRestMapper, AddBookTest.this.addBookUseCase);
    }

    @Test
    @DisplayName(
        "POST " + ADD_BOOK_ENDPOINT_PATH + " returns HTTP 422 when book structure is invalid")
    void shouldReturn422_whenBookStructureInvalid() throws Exception {
      // GIVEN
      final String invalidBookRequestDtoJson =
          """
        {
          "isbn": "9780747532743",
          "title": "Effective Java",
          "author": "Joshua Bloch",
          "publisher": "Addison-Wesley",
          "year": 2008,
          "price": "21.9900",
          "genre": "FICTION",
          "type": "SALE"
        }
        """;

      // WHEN
      final MvcResult result =
          AddBookTest.this
              .mockMvc
              .perform(
                  post(ADD_BOOK_ENDPOINT_PATH)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(invalidBookRequestDtoJson))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus())
          .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(AddBookTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.UNPROCESSABLE_ENTITY),
              any(HttpMessageNotReadableException.class),
              any(WebRequest.class));
      verifyNoMoreInteractions(AddBookTest.this.bookRestMapper, AddBookTest.this.addBookUseCase);
    }
  }
}
