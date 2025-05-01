package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.InventoryController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.inventory.base.BaseInventoryTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.InventoryRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.DeleteOldCopiesFromInventoryUseCase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(SpringExtension.class)
class RemoveOldBookCopiesTest extends BaseInventoryTestController {

  private static final String REMOVE_OLD_COPIES_ENDPOINT_PATH = "/v1/inventory/books/older-than";

  @InjectMocks private InventoryController inventoryController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private DeleteOldCopiesFromInventoryUseCase deleteOldCopiesFromInventoryUseCase;

  @Mock private InventoryRestMapper inventoryRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(
        this.inventoryController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "DELETE "
          + REMOVE_OLD_COPIES_ENDPOINT_PATH
          + " returns HTTP 204 when old copies are removed successfully")
  void shouldReturn204_whenOldCopiesRemovedSuccessfully() throws Exception {
    // GIVEN
    final LocalDateTime date = LocalDateTime.now();
    final String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_OLD_COPIES_ENDPOINT_PATH)
                    .param("date", dateStr)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

    verify(this.deleteOldCopiesFromInventoryUseCase).deleteOldCopies(date);
    verifyNoInteractions(this.inventoryRestMapper, this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "DELETE "
          + REMOVE_OLD_COPIES_ENDPOINT_PATH
          + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final LocalDateTime date = LocalDateTime.now();
    final String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    final NullPointerException exception = new NullPointerException("Unexpected error");

    doThrow(exception).when(this.deleteOldCopiesFromInventoryUseCase).deleteOldCopies(date);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_OLD_COPIES_ENDPOINT_PATH)
                    .param("date", dateStr)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.deleteOldCopiesFromInventoryUseCase).deleteOldCopies(date);
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoInteractions(this.inventoryRestMapper);
  }

  @Nested
  @DisplayName("DELETE " + REMOVE_OLD_COPIES_ENDPOINT_PATH + " returns HTTP 4xx on client cases")
  class ClientCases {

    @Test
    @DisplayName(
        "DELETE " + REMOVE_OLD_COPIES_ENDPOINT_PATH + " returns HTTP 400 when date is invalid")
    void shouldReturn400_whenDateInvalid() throws Exception {
      // GIVEN
      final String invalidDate = "not-a-date";

      // WHEN
      final MvcResult result =
          RemoveOldBookCopiesTest.this
              .mockMvc
              .perform(
                  delete(REMOVE_OLD_COPIES_ENDPOINT_PATH)
                      .param("date", invalidDate)
                      .contentType(MediaType.APPLICATION_JSON))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(RemoveOldBookCopiesTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.BAD_REQUEST),
              any(MethodArgumentTypeMismatchException.class),
              any(WebRequest.class));

      verifyNoInteractions(
          RemoveOldBookCopiesTest.this.deleteOldCopiesFromInventoryUseCase,
          RemoveOldBookCopiesTest.this.inventoryRestMapper);
    }

    @Test
    @DisplayName(
        "DELETE " + REMOVE_OLD_COPIES_ENDPOINT_PATH + " returns HTTP 400 when date is missing")
    void shouldReturn400_whenDateMissing() throws Exception {
      // GIVEN
      // No date parameter provided

      // WHEN
      final MvcResult result =
          RemoveOldBookCopiesTest.this
              .mockMvc
              .perform(
                  delete(REMOVE_OLD_COPIES_ENDPOINT_PATH).contentType(MediaType.APPLICATION_JSON))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(RemoveOldBookCopiesTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.BAD_REQUEST),
              any(MissingServletRequestParameterException.class),
              any(WebRequest.class));

      verifyNoInteractions(
          RemoveOldBookCopiesTest.this.deleteOldCopiesFromInventoryUseCase,
          RemoveOldBookCopiesTest.this.inventoryRestMapper);
    }
  }
}
