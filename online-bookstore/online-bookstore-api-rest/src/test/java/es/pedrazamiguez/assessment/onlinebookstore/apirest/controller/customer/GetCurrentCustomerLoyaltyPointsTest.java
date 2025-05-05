package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.CustomerController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.customer.base.BaseCustomerTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.CustomerRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.loyalty.GetLoyaltyPointsUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.LoyaltyPointsDto;
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

@ExtendWith(SpringExtension.class)
class GetCurrentCustomerLoyaltyPointsTest extends BaseCustomerTestController {

  private static final String GET_CURRENT_LOYALTY_POINTS_PATH = "/v1/customers/loyalty-points";

  @InjectMocks private CustomerController customerController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private GetLoyaltyPointsUseCase getLoyaltyPointsUseCase;

  @Mock private CustomerRestMapper customerRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(this.customerController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName("GET " + GET_CURRENT_LOYALTY_POINTS_PATH + " returns HTTP 200 with LoyaltyPointsDto")
  void shouldReturn200AndLoyaltyPointsDto_whenPointsRetrieved() throws Exception {
    // GIVEN
    final Long points = this.givenLoyaltyPoints();
    final LoyaltyPointsDto loyaltyPointsDto = this.givenLoyaltyPointsDto();

    when(this.getLoyaltyPointsUseCase.getCurrentCustomerLoyaltyPoints()).thenReturn(points);
    when(this.customerRestMapper.toLoyaltyPointsDto(points)).thenReturn(loyaltyPointsDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(GET_CURRENT_LOYALTY_POINTS_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(loyaltyPointsDto));

    verify(this.getLoyaltyPointsUseCase).getCurrentCustomerLoyaltyPoints();
    verify(this.customerRestMapper).toLoyaltyPointsDto(points);
    verifyNoMoreInteractions(this.getLoyaltyPointsUseCase, this.customerRestMapper);
  }

  @Test
  @DisplayName(
      "GET " + GET_CURRENT_LOYALTY_POINTS_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final RuntimeException exception = new RuntimeException("Unexpected error");
    when(this.getLoyaltyPointsUseCase.getCurrentCustomerLoyaltyPoints()).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(get(GET_CURRENT_LOYALTY_POINTS_PATH).contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.getLoyaltyPointsUseCase).getCurrentCustomerLoyaltyPoints();
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), any(RuntimeException.class), any());
    verifyNoMoreInteractions(this.getLoyaltyPointsUseCase, this.errorRestMapper);
    verifyNoInteractions(this.customerRestMapper);
  }
}
