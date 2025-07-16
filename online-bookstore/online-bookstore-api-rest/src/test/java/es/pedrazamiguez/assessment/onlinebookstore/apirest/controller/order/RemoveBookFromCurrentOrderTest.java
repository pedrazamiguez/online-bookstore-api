package es.pedrazamiguez.api.onlinebookstore.apirest.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import es.pedrazamiguez.api.onlinebookstore.apirest.controller.OrderController;
import es.pedrazamiguez.api.onlinebookstore.apirest.controller.order.base.BaseOrderTestController;
import es.pedrazamiguez.api.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.api.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.api.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.api.onlinebookstore.apirest.mapper.OrderRestMapper;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotInOrderException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.usecase.order.RemoveFromOrderUseCase;
import es.pedrazamiguez.api.onlinebookstore.openapi.model.OrderDto;
import java.util.Optional;
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

@ExtendWith(SpringExtension.class)
class RemoveBookFromCurrentOrderTest extends BaseOrderTestController {

  private static final String REMOVE_BOOK_FROM_ORDER_PATH = "/v1/orders/current/books/{bookId}";

  @InjectMocks private OrderController orderController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private RemoveFromOrderUseCase removeFromOrderUseCase;

  @Mock private OrderRestMapper orderRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.orderController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "DELETE "
          + REMOVE_BOOK_FROM_ORDER_PATH
          + " returns HTTP 200 with OrderDto when book is removed")
  void shouldReturn200AndOrderDto_whenBookRemoved() throws Exception {
    // GIVEN
    final Long bookId = 1L;
    final Long copies = 2L;
    final Optional<Order> order = this.givenOptionalOrder();
    final OrderDto orderDto = this.givenOrderDto();

    when(this.removeFromOrderUseCase.removeFromOrder(bookId, copies)).thenReturn(order);
    when(this.orderRestMapper.toDto(order.get())).thenReturn(orderDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_BOOK_FROM_ORDER_PATH, bookId)
                    .queryParam("copies", copies.toString())
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(orderDto));

    verify(this.removeFromOrderUseCase).removeFromOrder(bookId, copies);
    verify(this.orderRestMapper).toDto(order.get());
    verifyNoMoreInteractions(this.removeFromOrderUseCase, this.orderRestMapper);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "DELETE " + REMOVE_BOOK_FROM_ORDER_PATH + " returns HTTP 204 when no order is returned")
  void shouldReturn204_whenNoOrderReturned() throws Exception {
    // GIVEN
    final Long bookId = 1L;
    final Long copies = 2L;

    when(this.removeFromOrderUseCase.removeFromOrder(bookId, copies))
        .thenReturn(this.givenEmptyOptionalOrder());

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_BOOK_FROM_ORDER_PATH, bookId)
                    .queryParam("copies", copies.toString())
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    assertThat(result.getResponse().getContentAsString()).isEmpty();

    verify(this.removeFromOrderUseCase).removeFromOrder(bookId, copies);
    verifyNoMoreInteractions(this.removeFromOrderUseCase);
    verifyNoInteractions(this.orderRestMapper, this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "DELETE " + REMOVE_BOOK_FROM_ORDER_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final Long bookId = 1L;
    final Long copies = 2L;
    final RuntimeException exception = new RuntimeException("Unexpected error");

    when(this.removeFromOrderUseCase.removeFromOrder(bookId, copies)).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                delete(REMOVE_BOOK_FROM_ORDER_PATH, bookId)
                    .queryParam("copies", copies.toString())
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.removeFromOrderUseCase).removeFromOrder(bookId, copies);
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoMoreInteractions(this.removeFromOrderUseCase);
    verifyNoInteractions(this.orderRestMapper);
  }

  @Nested
  @DisplayName("DELETE " + REMOVE_BOOK_FROM_ORDER_PATH + " returns HTTP 4xx on client errors")
  class ClientErrors {

    @Test
    @DisplayName(
        "DELETE " + REMOVE_BOOK_FROM_ORDER_PATH + " returns HTTP 409 when book is not in order")
    void shouldReturn409_whenBookNotInOrder() throws Exception {
      // GIVEN
      final Long bookId = 1L;
      final Long copies = 2L;
      final BookNotInOrderException exception = new BookNotInOrderException(bookId, 1L);

      when(RemoveBookFromCurrentOrderTest.this.removeFromOrderUseCase.removeFromOrder(
              bookId, copies))
          .thenThrow(exception);

      // WHEN
      final MvcResult result =
          RemoveBookFromCurrentOrderTest.this
              .mockMvc
              .perform(
                  delete(REMOVE_BOOK_FROM_ORDER_PATH, bookId)
                      .queryParam("copies", copies.toString())
                      .contentType(MediaType.APPLICATION_JSON))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(RemoveBookFromCurrentOrderTest.this.removeFromOrderUseCase)
          .removeFromOrder(bookId, copies);
      verify(RemoveBookFromCurrentOrderTest.this.errorRestMapper)
          .toDto(eq(HttpStatus.CONFLICT), eq(exception), any(WebRequest.class));
      verifyNoMoreInteractions(RemoveBookFromCurrentOrderTest.this.removeFromOrderUseCase);
      verifyNoInteractions(RemoveBookFromCurrentOrderTest.this.orderRestMapper);
    }
  }
}
