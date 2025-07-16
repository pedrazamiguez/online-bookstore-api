package es.pedrazamiguez.onlinebookstore.apirest.controller.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import es.pedrazamiguez.onlinebookstore.apirest.controller.CustomerController;
import es.pedrazamiguez.onlinebookstore.apirest.controller.customer.base.BaseCustomerTestController;
import es.pedrazamiguez.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.ErrorRestMapper;
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
class TemporaryCustomerTest extends BaseCustomerTestController {

  private static final String GET_CUSTOMER_LOYALTY_POINTS_PATH =
      "/v1/customers/{customerId}/loyalty-points";

  @InjectMocks private CustomerController customerController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.customerController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName("GET " + GET_CUSTOMER_LOYALTY_POINTS_PATH + " returns HTTP 501 when not implemented")
  void shouldReturn501_whenGetCustomerLoyaltyPointsNotImplemented() throws Exception {
    // GIVEN
    final Long customerId = 1L;

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                get(GET_CUSTOMER_LOYALTY_POINTS_PATH, customerId)
                    .contentType(MediaType.APPLICATION_JSON))
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
