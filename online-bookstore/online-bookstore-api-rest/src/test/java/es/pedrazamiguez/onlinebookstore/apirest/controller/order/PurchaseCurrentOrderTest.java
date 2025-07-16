package es.pedrazamiguez.onlinebookstore.apirest.controller.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import es.pedrazamiguez.onlinebookstore.apirest.controller.OrderController;
import es.pedrazamiguez.onlinebookstore.apirest.controller.order.base.BaseOrderTestController;
import es.pedrazamiguez.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.OrderRestMapper;
import es.pedrazamiguez.onlinebookstore.domain.exception.NotEnoughBookCopiesException;
import es.pedrazamiguez.onlinebookstore.domain.exception.OrderContainsNoItemsException;
import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.usecase.purchase.PerformPurchaseUseCase;
import es.pedrazamiguez.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.onlinebookstore.openapi.model.PurchaseRequestDto;
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
class PurchaseCurrentOrderTest extends BaseOrderTestController {

  private static final String PURCHASE_CURRENT_ORDER_PATH = "/v1/orders/current/purchase";

  @InjectMocks private OrderController orderController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private PerformPurchaseUseCase performPurchaseUseCase;

  @Mock private OrderRestMapper orderRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.orderController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "POST "
          + PURCHASE_CURRENT_ORDER_PATH
          + " returns HTTP 200 with OrderDto when purchase succeeds")
  void shouldReturn200AndOrderDto_whenPurchaseSucceeds() throws Exception {
    // GIVEN
    final PurchaseRequestDto purchaseRequestDto = this.givenPurchaseRequestDto();
    final Order orderRequest = this.givenOrder();
    final Order purchasedOrder = this.givenOrder();
    final OrderDto orderDto = this.givenOrderDto();

    when(this.orderRestMapper.toDomain(purchaseRequestDto)).thenReturn(orderRequest);
    when(this.performPurchaseUseCase.purchase(orderRequest)).thenReturn(purchasedOrder);
    when(this.orderRestMapper.toDto(purchasedOrder)).thenReturn(orderDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                post(PURCHASE_CURRENT_ORDER_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(purchaseRequestDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(orderDto));

    verify(this.orderRestMapper).toDomain(purchaseRequestDto);
    verify(this.performPurchaseUseCase).purchase(orderRequest);
    verify(this.orderRestMapper).toDto(purchasedOrder);
    verifyNoMoreInteractions(this.performPurchaseUseCase, this.orderRestMapper);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "POST " + PURCHASE_CURRENT_ORDER_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final PurchaseRequestDto purchaseRequestDto = this.givenPurchaseRequestDto();
    final Order orderRequest = this.givenOrder();
    final RuntimeException exception = new RuntimeException("Unexpected error");

    when(this.orderRestMapper.toDomain(purchaseRequestDto)).thenReturn(orderRequest);
    when(this.performPurchaseUseCase.purchase(orderRequest)).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                post(PURCHASE_CURRENT_ORDER_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(purchaseRequestDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.orderRestMapper).toDomain(purchaseRequestDto);
    verify(this.performPurchaseUseCase).purchase(orderRequest);
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoMoreInteractions(this.performPurchaseUseCase, this.orderRestMapper);
  }

  @Nested
  @DisplayName("POST " + PURCHASE_CURRENT_ORDER_PATH + " returns HTTP 4xx on client errors")
  class ClientErrors {

    @Test
    @DisplayName(
        "POST "
            + PURCHASE_CURRENT_ORDER_PATH
            + " returns HTTP 409 when not enough copies in inventory")
    void shouldReturn409_whenNotEnoughCopies() throws Exception {
      // GIVEN
      final PurchaseRequestDto purchaseRequestDto =
          PurchaseCurrentOrderTest.this.givenPurchaseRequestDto();
      final Order orderRequest = PurchaseCurrentOrderTest.this.givenOrder();
      final RuntimeException exception = new NotEnoughBookCopiesException(1L, 0L, 3L);

      when(PurchaseCurrentOrderTest.this.orderRestMapper.toDomain(purchaseRequestDto))
          .thenReturn(orderRequest);
      when(PurchaseCurrentOrderTest.this.performPurchaseUseCase.purchase(orderRequest))
          .thenThrow(exception);

      // WHEN
      final MvcResult result =
          PurchaseCurrentOrderTest.this
              .mockMvc
              .perform(
                  post(PURCHASE_CURRENT_ORDER_PATH)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          PurchaseCurrentOrderTest.this.objectMapper.writeValueAsString(
                              purchaseRequestDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(PurchaseCurrentOrderTest.this.orderRestMapper).toDomain(purchaseRequestDto);
      verify(PurchaseCurrentOrderTest.this.performPurchaseUseCase).purchase(orderRequest);
      verify(PurchaseCurrentOrderTest.this.errorRestMapper)
          .toDto(eq(HttpStatus.CONFLICT), eq(exception), any(WebRequest.class));
      verifyNoMoreInteractions(
          PurchaseCurrentOrderTest.this.performPurchaseUseCase,
          PurchaseCurrentOrderTest.this.orderRestMapper);
    }

    @Test
    @DisplayName(
        "POST " + PURCHASE_CURRENT_ORDER_PATH + " returns HTTP 412 when order contains no items")
    void shouldReturn412_whenOrderContainsNoItems() throws Exception {
      // GIVEN
      final PurchaseRequestDto purchaseRequestDto =
          PurchaseCurrentOrderTest.this.givenPurchaseRequestDto();
      final Order orderRequest = PurchaseCurrentOrderTest.this.givenOrder();
      final RuntimeException exception = new OrderContainsNoItemsException(orderRequest.getId());

      when(PurchaseCurrentOrderTest.this.orderRestMapper.toDomain(purchaseRequestDto))
          .thenReturn(orderRequest);
      when(PurchaseCurrentOrderTest.this.performPurchaseUseCase.purchase(orderRequest))
          .thenThrow(exception);

      // WHEN
      final MvcResult result =
          PurchaseCurrentOrderTest.this
              .mockMvc
              .perform(
                  post(PURCHASE_CURRENT_ORDER_PATH)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          PurchaseCurrentOrderTest.this.objectMapper.writeValueAsString(
                              purchaseRequestDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus())
          .isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(PurchaseCurrentOrderTest.this.orderRestMapper).toDomain(purchaseRequestDto);
      verify(PurchaseCurrentOrderTest.this.performPurchaseUseCase).purchase(orderRequest);
      verify(PurchaseCurrentOrderTest.this.errorRestMapper)
          .toDto(eq(HttpStatus.PRECONDITION_FAILED), eq(exception), any(WebRequest.class));
      verifyNoMoreInteractions(
          PurchaseCurrentOrderTest.this.performPurchaseUseCase,
          PurchaseCurrentOrderTest.this.orderRestMapper);
    }
  }
}
