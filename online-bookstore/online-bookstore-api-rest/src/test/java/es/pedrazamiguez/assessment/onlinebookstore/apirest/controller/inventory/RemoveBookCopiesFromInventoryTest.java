package es.pedrazamiguez.api.onlinebookstore.apirest.controller.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import es.pedrazamiguez.api.onlinebookstore.apirest.controller.InventoryController;
import es.pedrazamiguez.api.onlinebookstore.apirest.controller.inventory.base.BaseInventoryTestController;
import es.pedrazamiguez.api.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.api.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.api.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.api.onlinebookstore.apirest.mapper.InventoryRestMapper;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.api.onlinebookstore.domain.usecase.inventory.DeleteFromInventoryUseCase;
import es.pedrazamiguez.api.onlinebookstore.openapi.model.InventoryItemDto;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(SpringExtension.class)
class RemoveBookCopiesFromInventoryTest extends BaseInventoryTestController {

  private static final String REMOVE_BOOK_COPIES_ENDPOINT_PATH = "/v1/inventory/books/{bookId}";

  @InjectMocks private InventoryController inventoryController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private DeleteFromInventoryUseCase deleteFromInventoryUseCase;

  @Mock private InventoryRestMapper inventoryRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(
        this.inventoryController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @ParameterizedTest
  @ValueSource(longs = {1, 10, 100})
  @DisplayName(
      "DELETE "
          + REMOVE_BOOK_COPIES_ENDPOINT_PATH
          + " returns HTTP 200 with InventoryItemDto when copies are removed successfully")
  void shouldReturn200AndInventoryItemDto_whenCopiesRemovedSuccessfully(final Long copies)
      throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final BookAllocation bookAllocation = this.givenBookAllocation(bookId, copies);
    final InventoryItemDto expectedDto = this.givenInventoryItemDto(bookId, copies);

    when(this.deleteFromInventoryUseCase.deleteFromInventory(bookId, copies))
        .thenReturn(Optional.of(bookAllocation));
    when(this.inventoryRestMapper.inventoryDetailsToInventoryItemDto(bookAllocation))
        .thenReturn(expectedDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_BOOK_COPIES_ENDPOINT_PATH, bookId)
                    .param("copies", copies.toString())
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(expectedDto));
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.deleteFromInventoryUseCase).deleteFromInventory(bookId, copies);
    verify(this.inventoryRestMapper).inventoryDetailsToInventoryItemDto(bookAllocation);
    verifyNoMoreInteractions(this.deleteFromInventoryUseCase, this.inventoryRestMapper);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "DELETE " + REMOVE_BOOK_COPIES_ENDPOINT_PATH + " returns HTTP 204 when no copies are removed")
  void shouldReturn204_whenNoCopiesRemoved() throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final Long copies = this.createRandomCopies();

    when(this.deleteFromInventoryUseCase.deleteFromInventory(bookId, copies))
        .thenReturn(Optional.empty());

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_BOOK_COPIES_ENDPOINT_PATH, bookId)
                    .param("copies", copies.toString())
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

    verify(this.deleteFromInventoryUseCase).deleteFromInventory(bookId, copies);
    verifyNoInteractions(this.inventoryRestMapper, this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "DELETE "
          + REMOVE_BOOK_COPIES_ENDPOINT_PATH
          + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final Long copies = this.createRandomCopies();
    final NullPointerException exception = new NullPointerException("Unexpected error");

    when(this.deleteFromInventoryUseCase.deleteFromInventory(bookId, copies)).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_BOOK_COPIES_ENDPOINT_PATH, bookId)
                    .param("copies", copies.toString())
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.deleteFromInventoryUseCase).deleteFromInventory(bookId, copies);
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoInteractions(this.inventoryRestMapper);
  }

  @Nested
  @DisplayName("DELETE " + REMOVE_BOOK_COPIES_ENDPOINT_PATH + " returns HTTP 4xx on client cases")
  class ClientCases {

    @Test
    @DisplayName(
        "DELETE " + REMOVE_BOOK_COPIES_ENDPOINT_PATH + " returns HTTP 400 when copies is invalid")
    void shouldReturn400_whenCopiesInvalid() throws Exception {
      // GIVEN
      final Long bookId = Instancio.create(Long.class);
      final String invalidCopies = "not-a-number";

      // WHEN
      final MvcResult result =
          RemoveBookCopiesFromInventoryTest.this
              .mockMvc
              .perform(
                  delete(REMOVE_BOOK_COPIES_ENDPOINT_PATH, bookId)
                      .param("copies", invalidCopies)
                      .contentType(MediaType.APPLICATION_JSON))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(RemoveBookCopiesFromInventoryTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.BAD_REQUEST),
              any(MethodArgumentTypeMismatchException.class),
              any(WebRequest.class));

      verifyNoInteractions(
          RemoveBookCopiesFromInventoryTest.this.deleteFromInventoryUseCase,
          RemoveBookCopiesFromInventoryTest.this.inventoryRestMapper);
    }

    @Test
    @DisplayName(
        "DELETE " + REMOVE_BOOK_COPIES_ENDPOINT_PATH + " returns HTTP 404 when book not found")
    void shouldReturn404_whenBookNotFound() throws Exception {
      // GIVEN
      final Long bookId = Instancio.create(Long.class);
      final Long copies = RemoveBookCopiesFromInventoryTest.this.createRandomCopies();
      final BookNotFoundException exception = new BookNotFoundException(bookId);

      when(RemoveBookCopiesFromInventoryTest.this.deleteFromInventoryUseCase.deleteFromInventory(
              bookId, copies))
          .thenThrow(exception);

      // WHEN
      final MvcResult result =
          RemoveBookCopiesFromInventoryTest.this
              .mockMvc
              .perform(
                  delete(REMOVE_BOOK_COPIES_ENDPOINT_PATH, bookId)
                      .param("copies", copies.toString())
                      .contentType(MediaType.APPLICATION_JSON))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(RemoveBookCopiesFromInventoryTest.this.deleteFromInventoryUseCase)
          .deleteFromInventory(bookId, copies);
      verify(RemoveBookCopiesFromInventoryTest.this.errorRestMapper)
          .toDto(eq(HttpStatus.NOT_FOUND), eq(exception), any(WebRequest.class));
      verifyNoInteractions(RemoveBookCopiesFromInventoryTest.this.inventoryRestMapper);
    }
  }
}
