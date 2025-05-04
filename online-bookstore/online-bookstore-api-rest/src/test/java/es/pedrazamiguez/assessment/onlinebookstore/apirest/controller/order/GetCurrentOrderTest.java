package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.OrderController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.order.base.BaseOrderTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.OrderRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.ViewOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import java.util.Optional;
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
class GetCurrentOrderTest extends BaseOrderTestController {

  private static final String GET_CURRENT_ORDER_PATH = "/v1/orders/current";

  @InjectMocks private OrderController orderController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private ViewOrderUseCase viewOrderUseCase;

  @Mock private OrderRestMapper orderRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.orderController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "GET " + GET_CURRENT_ORDER_PATH + " returns HTTP 200 with OrderDto when order exists")
  void shouldReturn200AndOrderDto_whenOrderExists() throws Exception {
    // GIVEN
    final Optional<Order> order = this.givenOptionalOrder();
    final OrderDto orderDto = this.givenOrderDto();

    when(this.viewOrderUseCase.getCurrentOrderForCustomer()).thenReturn(order);
    when(this.orderRestMapper.toDto(order.get())).thenReturn(orderDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(GET_CURRENT_ORDER_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(orderDto));

    verify(this.viewOrderUseCase).getCurrentOrderForCustomer();
    verify(this.orderRestMapper).toDto(order.get());
    verifyNoMoreInteractions(this.viewOrderUseCase, this.orderRestMapper);
  }

  @Test
  @DisplayName("GET " + GET_CURRENT_ORDER_PATH + " returns HTTP 204 when order is empty")
  void shouldReturn204_whenOrderIsEmpty() throws Exception {
    // GIVEN
    when(this.viewOrderUseCase.getCurrentOrderForCustomer())
        .thenReturn(this.givenEmptyOptionalOrder());

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(GET_CURRENT_ORDER_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    assertThat(result.getResponse().getContentAsString()).isEmpty();

    verify(this.viewOrderUseCase).getCurrentOrderForCustomer();
    verifyNoMoreInteractions(this.viewOrderUseCase);
    verifyNoInteractions(this.orderRestMapper);
  }

  @Test
  @DisplayName("GET " + GET_CURRENT_ORDER_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final RuntimeException exception = new RuntimeException("Unexpected error");
    when(this.viewOrderUseCase.getCurrentOrderForCustomer()).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(GET_CURRENT_ORDER_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.viewOrderUseCase).getCurrentOrderForCustomer();
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoMoreInteractions(this.viewOrderUseCase);
    verifyNoInteractions(this.orderRestMapper);
  }
}
