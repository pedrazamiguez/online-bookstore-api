package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.OrderController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.order.base.BaseOrderTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.OrderRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.NotEnoughBookCopiesException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.AddToOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(SpringExtension.class)
class AddBookToCurrentOrderTest extends BaseOrderTestController {

  private static final String ADD_BOOK_TO_ORDER_PATH = "/v1/orders/current/books/{bookId}";

  @InjectMocks private OrderController orderController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private AddToOrderUseCase addToOrderUseCase;

  @Mock private OrderRestMapper orderRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.orderController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "PUT " + ADD_BOOK_TO_ORDER_PATH + " returns HTTP 200 with OrderDto when book is added")
  void shouldReturn200AndOrderDto_whenBookAdded() throws Exception {
    // GIVEN
    final Long bookId = 1L;
    final AllocationDto allocationDto = this.givenAllocationDto();
    final Order order = this.givenOrder();
    final OrderDto orderDto = this.givenOrderDto();

    when(this.addToOrderUseCase.addToOrder(bookId, allocationDto.getCopies())).thenReturn(order);
    when(this.orderRestMapper.toDto(order)).thenReturn(orderDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                put(ADD_BOOK_TO_ORDER_PATH, bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(allocationDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(orderDto));

    verify(this.addToOrderUseCase).addToOrder(bookId, allocationDto.getCopies());
    verify(this.orderRestMapper).toDto(order);
    verifyNoMoreInteractions(this.addToOrderUseCase, this.orderRestMapper);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName("PUT " + ADD_BOOK_TO_ORDER_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final Long bookId = 1L;
    final AllocationDto allocationDto = this.givenAllocationDto();
    final RuntimeException exception = new RuntimeException("Unexpected error");

    when(this.addToOrderUseCase.addToOrder(bookId, allocationDto.getCopies())).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                put(ADD_BOOK_TO_ORDER_PATH, bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(allocationDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.addToOrderUseCase).addToOrder(bookId, allocationDto.getCopies());
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoMoreInteractions(this.addToOrderUseCase);
    verifyNoInteractions(this.orderRestMapper);
  }

  @Nested
  @DisplayName("PUT " + ADD_BOOK_TO_ORDER_PATH + " returns HTTP 4xx on client errors")
  class ClientErrors {

    @Test
    @DisplayName("PUT " + ADD_BOOK_TO_ORDER_PATH + " returns HTTP 400 when request body is invalid")
    void shouldReturn400_whenRequestBodyInvalid() throws Exception {
      // GIVEN
      final Long bookId = 1L;
      final AllocationDto invalidAllocationDto = new AllocationDto(); // Missing copies

      // WHEN
      final MvcResult result =
          AddBookToCurrentOrderTest.this
              .mockMvc
              .perform(
                  put(ADD_BOOK_TO_ORDER_PATH, bookId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          AddBookToCurrentOrderTest.this.objectMapper.writeValueAsString(
                              invalidAllocationDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(AddBookToCurrentOrderTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.BAD_REQUEST),
              any(MethodArgumentNotValidException.class),
              any(WebRequest.class));
      verifyNoInteractions(
          AddBookToCurrentOrderTest.this.addToOrderUseCase,
          AddBookToCurrentOrderTest.this.orderRestMapper);
    }

    @Test
    @DisplayName(
        "PUT " + ADD_BOOK_TO_ORDER_PATH + " returns HTTP 409 when not enough copies in inventory")
    void shouldReturn409_whenNotEnoughCopies() throws Exception {
      // GIVEN
      final Long bookId = 1L;
      final AllocationDto allocationDto = AddBookToCurrentOrderTest.this.givenAllocationDto();
      final RuntimeException exception =
          new NotEnoughBookCopiesException(bookId, 0L, allocationDto.getCopies());

      when(AddBookToCurrentOrderTest.this.addToOrderUseCase.addToOrder(
              bookId, allocationDto.getCopies()))
          .thenThrow(exception);

      // WHEN
      final MvcResult result =
          AddBookToCurrentOrderTest.this
              .mockMvc
              .perform(
                  put(ADD_BOOK_TO_ORDER_PATH, bookId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          AddBookToCurrentOrderTest.this.objectMapper.writeValueAsString(
                              allocationDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(AddBookToCurrentOrderTest.this.addToOrderUseCase)
          .addToOrder(bookId, allocationDto.getCopies());
      verify(AddBookToCurrentOrderTest.this.errorRestMapper)
          .toDto(eq(HttpStatus.CONFLICT), eq(exception), any(WebRequest.class));
      verifyNoMoreInteractions(AddBookToCurrentOrderTest.this.addToOrderUseCase);
      verifyNoInteractions(AddBookToCurrentOrderTest.this.orderRestMapper);
    }
  }
}
