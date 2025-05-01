package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.inventory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.InventoryController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.inventory.base.BaseInventoryTestController;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.BookstoreExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.handler.RestExceptionHandler;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.ErrorRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper.InventoryRestMapper;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.inventory.AddToInventoryUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.AllocationDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.InventoryItemDto;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(SpringExtension.class)
class AddBookCopiesToInventoryTest extends BaseInventoryTestController {

  private static final String ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH =
      "/v1/inventory/books/{bookId}";

  @InjectMocks private InventoryController inventoryController;
  @InjectMocks private RestExceptionHandler restExceptionHandler;
  @InjectMocks private BookstoreExceptionHandler bookstoreExceptionHandler;

  @Mock private AddToInventoryUseCase addToInventoryUseCase;
  @Mock private InventoryRestMapper inventoryRestMapper;
  @Mock private ErrorRestMapper errorRestMapper;

  @BeforeEach
  void setUp() {
    super.setUp(
        this.inventoryController, this.bookstoreExceptionHandler, this.restExceptionHandler);
  }

  @DisplayName(
      "PUT "
          + ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH
          + " returns HTTP 200 with InventoryItemDto when "
          + "copies are added successfully")
  @ParameterizedTest(name = "Using {0} copies")
  @ValueSource(longs = {1L, 10L, 100L, 1000L})
  void shouldReturn200AndInventoryItemDto_whenBookCopiesAddedSuccessfully(final Long copies)
      throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final AllocationDto allocationDto = this.givenAllocationDto(copies);

    final BookAllocation expectedBookAllocation = this.givenBookAllocation(bookId, copies);
    final InventoryItemDto expectedInventoryItemDto = this.givenInventoryItemDto(bookId, copies);

    when(this.addToInventoryUseCase.addToInventory(bookId, allocationDto.getCopies()))
        .thenReturn(Optional.of(expectedBookAllocation));
    when(this.inventoryRestMapper.inventoryDetailsToInventoryItemDto(expectedBookAllocation))
        .thenReturn(expectedInventoryItemDto);

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                put(ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH, bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(allocationDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(result.getResponse().getContentAsString())
        .isEqualTo(this.objectMapper.writeValueAsString(expectedInventoryItemDto));
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.addToInventoryUseCase).addToInventory(bookId, allocationDto.getCopies());
    verify(this.inventoryRestMapper).inventoryDetailsToInventoryItemDto(expectedBookAllocation);
    verifyNoMoreInteractions(this.addToInventoryUseCase, this.inventoryRestMapper);
  }

  @Test
  @DisplayName(
      "PUT "
          + ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH
          + " returns HTTP 204 when no book copies are "
          + "added")
  void shouldReturn204_whenNoBookCopiesAreAdded() throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final Long copies = this.createRandomCopies();
    final AllocationDto allocationDto = this.givenAllocationDto(copies);

    when(this.addToInventoryUseCase.addToInventory(bookId, allocationDto.getCopies()))
        .thenReturn(Optional.empty());

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                put(ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH, bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(allocationDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());

    verify(this.addToInventoryUseCase).addToInventory(bookId, allocationDto.getCopies());
    verifyNoInteractions(this.inventoryRestMapper, this.errorRestMapper);
  }

  @Test
  @DisplayName(
      "PUT "
          + ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH
          + " returns HTTP 500 when an unexpected error "
          + "occurs")
  void shouldReturn500_whenUnexpectedErrorOccurs() throws Exception {
    // GIVEN
    final Long bookId = Instancio.create(Long.class);
    final Long copies = this.createRandomCopies();
    final AllocationDto allocationDto = this.givenAllocationDto(copies);

    final NullPointerException exceptionThrown = new NullPointerException();
    doThrow(exceptionThrown)
        .when(this.addToInventoryUseCase)
        .addToInventory(bookId, allocationDto.getCopies());

    // WHEN
    final MvcResult result =
        this.mockMvc
            .perform(
                put(ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH, bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(allocationDto)))
            .andReturn();

    // THEN
    assertThat(result.getResponse().getStatus())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    assertThat(result.getResponse().getContentType())
        .isEqualTo(MediaType.APPLICATION_JSON.toString());

    verify(this.addToInventoryUseCase).addToInventory(bookId, allocationDto.getCopies());
    verify(this.errorRestMapper)
        .toDto(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq(exceptionThrown), any(WebRequest.class));
    verifyNoInteractions(this.inventoryRestMapper);
  }

  @Nested
  @DisplayName(
      "PUT " + ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH + " returns HTTP 4xx on client cases")
  class ClientCases {

    @Test
    @DisplayName(
        "PUT "
            + ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH
            + " returns HTTP 400 when request body is "
            + "invalid")
    void shouldReturn400_whenRequestBodyIsInvalid() throws Exception {
      // GIVEN
      final Long bookId = Instancio.create(Long.class);
      final AllocationDto allocationDto =
          AddBookCopiesToInventoryTest.this.givenAllocationDto(10000L);

      // WHEN
      final MvcResult result =
          AddBookCopiesToInventoryTest.this
              .mockMvc
              .perform(
                  put(ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH, bookId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          AddBookCopiesToInventoryTest.this.objectMapper.writeValueAsString(
                              allocationDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verifyNoInteractions(
          AddBookCopiesToInventoryTest.this.addToInventoryUseCase,
          AddBookCopiesToInventoryTest.this.inventoryRestMapper);
    }

    @Test
    @DisplayName(
        "PUT "
            + ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH
            + " returns HTTP 404 when book is not found")
    void shouldReturn404_whenBookNotFound() throws Exception {
      // GIVEN
      final Long bookId = Instancio.create(Long.class);
      final Long copies = AddBookCopiesToInventoryTest.this.createRandomCopies();
      final AllocationDto allocationDto =
          AddBookCopiesToInventoryTest.this.givenAllocationDto(copies);

      final BookNotFoundException exceptionThrown = new BookNotFoundException(bookId);
      doThrow(exceptionThrown)
          .when(AddBookCopiesToInventoryTest.this.addToInventoryUseCase)
          .addToInventory(bookId, allocationDto.getCopies());

      // WHEN
      final MvcResult result =
          AddBookCopiesToInventoryTest.this
              .mockMvc
              .perform(
                  put(ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH, bookId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(
                          AddBookCopiesToInventoryTest.this.objectMapper.writeValueAsString(
                              allocationDto)))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verify(AddBookCopiesToInventoryTest.this.addToInventoryUseCase)
          .addToInventory(bookId, allocationDto.getCopies());
      verify(AddBookCopiesToInventoryTest.this.errorRestMapper)
          .toDto(eq(HttpStatus.NOT_FOUND), eq(exceptionThrown), any(WebRequest.class));
      verifyNoInteractions(AddBookCopiesToInventoryTest.this.inventoryRestMapper);
    }

    @Test
    @DisplayName(
        "PUT "
            + ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH
            + " returns HTTP 422 when request body is "
            + "empty")
    void shouldReturn422_whenRequestBodyIsEmpty() throws Exception {
      // GIVEN
      final Long bookId = Instancio.create(Long.class);

      // WHEN
      final MvcResult result =
          AddBookCopiesToInventoryTest.this
              .mockMvc
              .perform(
                  put(ADD_BOOK_COPIES_TO_INVENTORY_ENDPOINT_PATH, bookId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(""))
              .andReturn();

      // THEN
      assertThat(result.getResponse().getStatus())
          .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
      assertThat(result.getResponse().getContentType())
          .isEqualTo(MediaType.APPLICATION_JSON.toString());

      verifyNoInteractions(
          AddBookCopiesToInventoryTest.this.addToInventoryUseCase,
          AddBookCopiesToInventoryTest.this.inventoryRestMapper);
    }
  }
}
