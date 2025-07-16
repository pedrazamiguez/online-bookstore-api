package es.pedrazamiguez.onlinebookstore.apirest.controller.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import es.pedrazamiguez.onlinebookstore.apirest.controller.InventoryController;
import es.pedrazamiguez.onlinebookstore.apirest.controller.inventory.base.BaseInventoryTestController;
import es.pedrazamiguez.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.onlinebookstore.apirest.mapper.InventoryRestMapper;
import es.pedrazamiguez.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.onlinebookstore.domain.usecase.inventory.GetInventoryStatusUseCase;
import es.pedrazamiguez.onlinebookstore.openapi.model.InventoryItemDto;
import java.util.Collections;
import java.util.List;
import org.instancio.Instancio;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(SpringExtension.class)
class GetInventoryTest extends BaseInventoryTestController {

  private static final String GET_INVENTORY_ENDPOINT_PATH = "/v1/inventory";

  @InjectMocks private InventoryController inventoryController;

  @InjectMocks private RestExceptionHandler restExceptionHandler;

  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private GetInventoryStatusUseCase getInventoryStatusUseCase;

  @Mock private InventoryRestMapper inventoryRestMapper;

  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(
        this.inventoryController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @Test
  @DisplayName(
      "GET "
          + GET_INVENTORY_ENDPOINT_PATH
          + " returns HTTP 200 with InventoryItemDto list when inventory is found")
  void shouldReturn200AndInventoryItemDtoList_whenInventoryFound() throws Exception {
    // GIVEN
    final List<BookAllocation> bookAllocations =
        Instancio.ofList(BookAllocation.class).size(3).create();
    final List<InventoryItemDto> expectedDtoList =
        Instancio.ofList(InventoryItemDto.class).size(3).create();

    when(this.getInventoryStatusUseCase.getInventoryStatus(true)).thenReturn(bookAllocations);
    when(this.inventoryRestMapper.toDtoList(bookAllocations)).thenReturn(expectedDtoList);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                get(GET_INVENTORY_ENDPOINT_PATH)
                    .param("includeOutOfStock", "true")
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(expectedDtoList));
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.getInventoryStatusUseCase).getInventoryStatus(true);
    verify(this.inventoryRestMapper).toDtoList(bookAllocations);
    verifyNoMoreInteractions(this.getInventoryStatusUseCase, this.inventoryRestMapper);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "GET "
          + GET_INVENTORY_ENDPOINT_PATH
          + " returns HTTP 200 with empty list when inventory is empty")
  void shouldReturn200AndEmptyList_whenInventoryEmpty() throws Exception {
    // GIVEN
    when(this.getInventoryStatusUseCase.getInventoryStatus(false))
        .thenReturn(Collections.emptyList());
    when(this.inventoryRestMapper.toDtoList(Collections.emptyList()))
        .thenReturn(Collections.emptyList());

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                get(GET_INVENTORY_ENDPOINT_PATH)
                    .param("includeOutOfStock", "false")
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(Collections.emptyList()));
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.getInventoryStatusUseCase).getInventoryStatus(false);
    verify(this.inventoryRestMapper).toDtoList(Collections.emptyList());
    verifyNoMoreInteractions(this.getInventoryStatusUseCase, this.inventoryRestMapper);
    verifyNoInteractions(this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "GET " + GET_INVENTORY_ENDPOINT_PATH + " returns HTTP 500 when unexpected error occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final NullPointerException exception = new NullPointerException("Unexpected error");
    when(this.getInventoryStatusUseCase.getInventoryStatus(true)).thenThrow(exception);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                get(GET_INVENTORY_ENDPOINT_PATH)
                    .param("includeOutOfStock", "true")
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.getInventoryStatusUseCase).getInventoryStatus(true);
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exception), any(WebRequest.class));
    verifyNoInteractions(this.inventoryRestMapper);
  }

  @Nested
  @DisplayName("GET " + GET_INVENTORY_ENDPOINT_PATH + " returns HTTP 4xx on client cases")
  class ClientCases {

    @Test
    @DisplayName(
        "GET "
            + GET_INVENTORY_ENDPOINT_PATH
            + " returns HTTP 400 when includeOutOfStock is invalid")
    void shouldReturn400_whenIncludeOutOfStockInvalid() throws Exception {
      // GIVEN
      final String invalidValue = "not-a-boolean";

      // WHEN
      final MvcResult result =
          GetInventoryTest.this
              .mockMvc
              .perform(
                  get(GET_INVENTORY_ENDPOINT_PATH)
                      .param("includeOutOfStock", invalidValue)
                      .contentType(MediaType.APPLICATION_JSON))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(GetInventoryTest.this.errorRestMapper)
          .toDto(
              eq(HttpStatus.BAD_REQUEST),
              any(MethodArgumentTypeMismatchException.class),
              any(WebRequest.class));

      verifyNoInteractions(
          GetInventoryTest.this.getInventoryStatusUseCase,
          GetInventoryTest.this.inventoryRestMapper);
    }
  }
}
