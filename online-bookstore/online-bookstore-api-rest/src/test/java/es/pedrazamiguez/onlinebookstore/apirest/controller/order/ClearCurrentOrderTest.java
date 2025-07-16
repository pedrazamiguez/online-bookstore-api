package es.pedrazamiguez.onlinebookstore.apirest.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import es.pedrazamiguez.onlinebookstore.apirest.controller.OrderController;
import es.pedrazamiguez.onlinebookstore.apirest.controller.order.base.BaseOrderTestController;
import es.pedrazamiguez.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.onlinebookstore.domain.usecase.order.ClearOrderUseCase;
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
class ClearCurrentOrderTest extends BaseOrderTestController {

  private static final String CLEAR_CURRENT_ORDER_PATH = "/v1/orders/current";

  @InjectMocks private OrderController orderController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private ClearOrderUseCase clearOrderUseCase;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.orderController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName("DELETE " + CLEAR_CURRENT_ORDER_PATH + " returns HTTP 204 when order is cleared")
  void shouldReturn204_whenOrderCleared() throws Exception {
    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(delete(CLEAR_CURRENT_ORDER_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    assertThat(result.getResponse().getContentAsString()).isEmpty();

    verify(this.clearOrderUseCase).clearOrderItems();
    verifyNoMoreInteractions(this.clearOrderUseCase);
  }

  @Test
  @DisplayName(
      "DELETE " + CLEAR_CURRENT_ORDER_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final RuntimeException exception = new RuntimeException("Unexpected error");
    doThrow(exception).when(this.clearOrderUseCase).clearOrderItems();

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(delete(CLEAR_CURRENT_ORDER_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.clearOrderUseCase).clearOrderItems();
    verify(this.errorRestMapper)
        .toDto(
            eq(HttpStatus.INTERNAL_SERVER_ERROR),
            any(RuntimeException.class),
            any(WebRequest.class));
    verifyNoMoreInteractions(this.clearOrderUseCase);
  }
}
