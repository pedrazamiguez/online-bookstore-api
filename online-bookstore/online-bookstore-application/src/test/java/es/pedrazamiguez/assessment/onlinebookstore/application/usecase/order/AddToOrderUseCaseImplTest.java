package es.pedrazamiguez.api.onlinebookstore.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.api.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.security.SecurityService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddToOrderUseCaseImplTest {

  @InjectMocks private AddToOrderUseCaseImpl addToOrderUseCase;

  @Mock private SecurityService securityService;

  @Mock private AvailableBookCopiesService availableBookCopiesService;

  @Mock private CurrentOrderService currentOrderService;

  @Mock private OrderRepository orderRepository;

  @Mock private FinalPriceService finalPriceService;

  private String username;
  private Long bookId;
  private Long copies;
  private Order existingOrder;
  private Order updatedOrder;

  @BeforeEach
  void setUp() {
    this.username = Instancio.create(String.class);
    this.bookId = Instancio.create(Long.class);
    this.copies = 3L;
    this.existingOrder =
        Instancio.of(Order.class).set(field(Order::getId), Instancio.create(Long.class)).create();
    this.updatedOrder =
        Instancio.of(Order.class).set(field(Order::getId), this.existingOrder.getId()).create();
  }

  @Test
  @DisplayName("addToOrder adds book copies to order successfully")
  void shouldAddToOrderSuccessfully() {
    // GIVEN
    when(AddToOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(AddToOrderUseCaseImplTest.this.username);
    doNothing()
        .when(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies);
    when(AddToOrderUseCaseImplTest.this.currentOrderService.getOrCreateOrder(
            AddToOrderUseCaseImplTest.this.username))
        .thenReturn(AddToOrderUseCaseImplTest.this.existingOrder);
    when(AddToOrderUseCaseImplTest.this.orderRepository.saveOrderItem(
            AddToOrderUseCaseImplTest.this.existingOrder.getId(),
            AddToOrderUseCaseImplTest.this.bookId,
            AddToOrderUseCaseImplTest.this.copies))
        .thenReturn(AddToOrderUseCaseImplTest.this.updatedOrder);
    doNothing()
        .when(AddToOrderUseCaseImplTest.this.finalPriceService)
        .calculate(AddToOrderUseCaseImplTest.this.updatedOrder);

    // WHEN
    final Order result =
        AddToOrderUseCaseImplTest.this.addToOrderUseCase.addToOrder(
            AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies);

    // THEN
    assertThat(result).isEqualTo(AddToOrderUseCaseImplTest.this.updatedOrder);
    verify(AddToOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies);
    verify(AddToOrderUseCaseImplTest.this.currentOrderService)
        .getOrCreateOrder(AddToOrderUseCaseImplTest.this.username);
    verify(AddToOrderUseCaseImplTest.this.orderRepository)
        .saveOrderItem(
            AddToOrderUseCaseImplTest.this.existingOrder.getId(),
            AddToOrderUseCaseImplTest.this.bookId,
            AddToOrderUseCaseImplTest.this.copies);
    verify(AddToOrderUseCaseImplTest.this.finalPriceService)
        .calculate(AddToOrderUseCaseImplTest.this.updatedOrder);
    verifyNoMoreInteractions(
        AddToOrderUseCaseImplTest.this.securityService,
        AddToOrderUseCaseImplTest.this.availableBookCopiesService,
        AddToOrderUseCaseImplTest.this.currentOrderService,
        AddToOrderUseCaseImplTest.this.orderRepository,
        AddToOrderUseCaseImplTest.this.finalPriceService);
  }

  @Test
  @DisplayName("addToOrder throws BookNotFoundException when book does not exist")
  void shouldThrowBookNotFoundException_whenBookDoesNotExist() {
    // GIVEN
    final BookNotFoundException exception =
        new BookNotFoundException(AddToOrderUseCaseImplTest.this.bookId);
    when(AddToOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(AddToOrderUseCaseImplTest.this.username);
    doThrow(exception)
        .when(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies);

    // WHEN
    assertThatThrownBy(
            () ->
                AddToOrderUseCaseImplTest.this.addToOrderUseCase.addToOrder(
                    AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies))
        .isInstanceOf(BookNotFoundException.class)
        .hasMessageContaining(AddToOrderUseCaseImplTest.this.bookId.toString());

    // THEN
    verify(AddToOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies);
    verifyNoInteractions(
        AddToOrderUseCaseImplTest.this.currentOrderService,
        AddToOrderUseCaseImplTest.this.orderRepository,
        AddToOrderUseCaseImplTest.this.finalPriceService);
  }

  @Test
  @DisplayName("addToOrder throws IllegalArgumentException when copies is negative")
  void shouldThrowIllegalArgumentException_whenCopiesNegative() {
    // GIVEN
    final Long negativeCopies = -1L;
    when(AddToOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(AddToOrderUseCaseImplTest.this.username);
    doThrow(new IllegalArgumentException("Copies must be positive"))
        .when(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, negativeCopies);

    // WHEN
    assertThatThrownBy(
            () ->
                AddToOrderUseCaseImplTest.this.addToOrderUseCase.addToOrder(
                    AddToOrderUseCaseImplTest.this.bookId, negativeCopies))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Copies must be positive");

    // THEN
    verify(AddToOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, negativeCopies);
    verifyNoInteractions(
        AddToOrderUseCaseImplTest.this.currentOrderService,
        AddToOrderUseCaseImplTest.this.orderRepository,
        AddToOrderUseCaseImplTest.this.finalPriceService);
  }

  @Test
  @DisplayName("addToOrder throws IllegalArgumentException when copies is zero")
  void shouldThrowIllegalArgumentException_whenCopiesZero() {
    // GIVEN
    final Long zeroCopies = 0L;
    when(AddToOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(AddToOrderUseCaseImplTest.this.username);
    doThrow(new IllegalArgumentException("Copies must be positive"))
        .when(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, zeroCopies);

    // WHEN
    assertThatThrownBy(
            () ->
                AddToOrderUseCaseImplTest.this.addToOrderUseCase.addToOrder(
                    AddToOrderUseCaseImplTest.this.bookId, zeroCopies))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Copies must be positive");

    // THEN
    verify(AddToOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, zeroCopies);
    verifyNoInteractions(
        AddToOrderUseCaseImplTest.this.currentOrderService,
        AddToOrderUseCaseImplTest.this.orderRepository,
        AddToOrderUseCaseImplTest.this.finalPriceService);
  }

  @Test
  @DisplayName("addToOrder throws SecurityException when user is not authenticated")
  void shouldThrowSecurityException_whenUserNotAuthenticated() {
    // GIVEN
    final SecurityException exception = new SecurityException("User not authenticated");
    when(AddToOrderUseCaseImplTest.this.securityService.getCurrentUserName()).thenThrow(exception);

    // WHEN
    assertThatThrownBy(
            () ->
                AddToOrderUseCaseImplTest.this.addToOrderUseCase.addToOrder(
                    AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies))
        .isInstanceOf(SecurityException.class)
        .hasMessage("User not authenticated");

    // THEN
    verify(AddToOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verifyNoInteractions(
        AddToOrderUseCaseImplTest.this.availableBookCopiesService,
        AddToOrderUseCaseImplTest.this.currentOrderService,
        AddToOrderUseCaseImplTest.this.orderRepository,
        AddToOrderUseCaseImplTest.this.finalPriceService);
  }

  @Test
  @DisplayName("addToOrder throws RuntimeException when repository fails")
  void shouldThrowRuntimeException_whenRepositoryFails() {
    // GIVEN
    final RuntimeException exception = new RuntimeException("Database error");
    when(AddToOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(AddToOrderUseCaseImplTest.this.username);
    doNothing()
        .when(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies);
    when(AddToOrderUseCaseImplTest.this.currentOrderService.getOrCreateOrder(
            AddToOrderUseCaseImplTest.this.username))
        .thenReturn(AddToOrderUseCaseImplTest.this.existingOrder);
    when(AddToOrderUseCaseImplTest.this.orderRepository.saveOrderItem(
            AddToOrderUseCaseImplTest.this.existingOrder.getId(),
            AddToOrderUseCaseImplTest.this.bookId,
            AddToOrderUseCaseImplTest.this.copies))
        .thenThrow(exception);

    // WHEN
    assertThatThrownBy(
            () ->
                AddToOrderUseCaseImplTest.this.addToOrderUseCase.addToOrder(
                    AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("Database error");

    // THEN
    verify(AddToOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(AddToOrderUseCaseImplTest.this.availableBookCopiesService)
        .assure(AddToOrderUseCaseImplTest.this.bookId, AddToOrderUseCaseImplTest.this.copies);
    verify(AddToOrderUseCaseImplTest.this.currentOrderService)
        .getOrCreateOrder(AddToOrderUseCaseImplTest.this.username);
    verify(AddToOrderUseCaseImplTest.this.orderRepository)
        .saveOrderItem(
            AddToOrderUseCaseImplTest.this.existingOrder.getId(),
            AddToOrderUseCaseImplTest.this.bookId,
            AddToOrderUseCaseImplTest.this.copies);
    verifyNoInteractions(AddToOrderUseCaseImplTest.this.finalPriceService);
  }
}
