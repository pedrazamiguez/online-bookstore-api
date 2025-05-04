package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.OrderController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.order.base.BaseOrderTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
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
class TemporaryOrderTest extends BaseOrderTestController {

  private static final String ORDER_HISTORY_PATH = "/v1/orders";
  private static final String ORDER_BY_ID_PATH = "/v1/orders/{orderId}";

  @InjectMocks private OrderController orderController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.orderController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName("GET " + ORDER_HISTORY_PATH + " returns HTTP 501 when not implemented")
  void shouldReturn501_whenGetOrderHistoryNotImplemented() throws Exception {
    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(ORDER_HISTORY_PATH).contentType(MediaType.APPLICATION_JSON))
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
    verifyNoMoreInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName("GET " + ORDER_BY_ID_PATH + " returns HTTP 501 when not implemented")
  void shouldReturn501_whenGetOrderByIdNotImplemented() throws Exception {
    // GIVEN
    final Long orderId = 1L;

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(ORDER_BY_ID_PATH, orderId).contentType(MediaType.APPLICATION_JSON))
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
    verifyNoMoreInteractions(this.errorRestMapper);
  }
}
