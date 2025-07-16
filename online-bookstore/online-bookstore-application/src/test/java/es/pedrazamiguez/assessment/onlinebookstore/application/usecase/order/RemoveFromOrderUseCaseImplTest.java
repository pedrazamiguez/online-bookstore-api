package es.pedrazamiguez.api.onlinebookstore.application.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.api.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.OrderRepository;
import es.pedrazamiguez.api.onlinebookstore.domain.service.book.AvailableBookCopiesService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.CurrentOrderService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.order.FinalPriceService;
import es.pedrazamiguez.api.onlinebookstore.domain.service.security.SecurityService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import nl.altindag.log.LogCaptor;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoveFromOrderUseCaseImplTest {

  @InjectMocks private RemoveFromOrderUseCaseImpl removeFromOrderUseCase;

  @Mock private SecurityService securityService;

  @Mock private AvailableBookCopiesService availableBookCopiesService;

  @Mock private CurrentOrderService currentOrderService;

  @Mock private OrderRepository orderRepository;

  @Mock private FinalPriceService finalPriceService;

  private LogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    this.logCaptor = LogCaptor.forClass(RemoveFromOrderUseCaseImpl.class);
  }

  @AfterEach
  void tearDown() {
    this.logCaptor.close();
  }

  @Test
  @DisplayName("removeFromOrder returns empty when no lines remain")
  void shouldReturnEmpty_whenNoLinesRemain() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Long bookId = 1L;
    final Long copies = 2L;
    final Order existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final Order updatedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .set(field(Order::getLines), Collections.emptyList())
            .create();

    when(RemoveFromOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(username);
    when(RemoveFromOrderUseCaseImplTest.this.currentOrderService.getOrCreateOrder(username))
        .thenReturn(existingOrder);
    when(RemoveFromOrderUseCaseImplTest.this.orderRepository.deleteOrderItem(
            existingOrder.getId(), bookId, copies))
        .thenReturn(updatedOrder);

    // WHEN
    final Optional<Order> result =
        RemoveFromOrderUseCaseImplTest.this.removeFromOrderUseCase.removeFromOrder(bookId, copies);

    // THEN
    assertThat(result).isEmpty();
    verify(RemoveFromOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(RemoveFromOrderUseCaseImplTest.this.currentOrderService).getOrCreateOrder(username);
    verify(RemoveFromOrderUseCaseImplTest.this.orderRepository)
        .deleteOrderItem(existingOrder.getId(), bookId, copies);
    verifyNoMoreInteractions(
        RemoveFromOrderUseCaseImplTest.this.securityService,
        RemoveFromOrderUseCaseImplTest.this.currentOrderService,
        RemoveFromOrderUseCaseImplTest.this.orderRepository);
    verifyNoInteractions(
        RemoveFromOrderUseCaseImplTest.this.finalPriceService,
        RemoveFromOrderUseCaseImplTest.this.availableBookCopiesService);

    final List<String> infoLogs = RemoveFromOrderUseCaseImplTest.this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains(
            "Removing",
            copies.toString(),
            "copies of bookId",
            bookId.toString(),
            "for user:",
            username);
  }

  @Test
  @DisplayName("removeFromOrder handles zero copies")
  void shouldHandleZeroCopies() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Long bookId = 1L;
    final Long copies = 0L;
    final Order existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final Order updatedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .set(field(Order::getLines), Instancio.ofList(OrderItem.class).size(1).create())
            .create();

    when(RemoveFromOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(username);
    when(RemoveFromOrderUseCaseImplTest.this.currentOrderService.getOrCreateOrder(username))
        .thenReturn(existingOrder);
    when(RemoveFromOrderUseCaseImplTest.this.orderRepository.deleteOrderItem(
            existingOrder.getId(), bookId, copies))
        .thenReturn(updatedOrder);

    // WHEN
    final Optional<Order> result =
        RemoveFromOrderUseCaseImplTest.this.removeFromOrderUseCase.removeFromOrder(bookId, copies);

    // THEN
    assertThat(result).isPresent().contains(updatedOrder);
    verify(RemoveFromOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(RemoveFromOrderUseCaseImplTest.this.currentOrderService).getOrCreateOrder(username);
    verify(RemoveFromOrderUseCaseImplTest.this.orderRepository)
        .deleteOrderItem(existingOrder.getId(), bookId, copies);
    verify(RemoveFromOrderUseCaseImplTest.this.finalPriceService).calculate(updatedOrder);
    verifyNoMoreInteractions(
        RemoveFromOrderUseCaseImplTest.this.securityService,
        RemoveFromOrderUseCaseImplTest.this.currentOrderService,
        RemoveFromOrderUseCaseImplTest.this.orderRepository,
        RemoveFromOrderUseCaseImplTest.this.finalPriceService);
    verifyNoInteractions(RemoveFromOrderUseCaseImplTest.this.availableBookCopiesService);

    final List<String> infoLogs = RemoveFromOrderUseCaseImplTest.this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains(
            "Removing",
            copies.toString(),
            "copies of bookId",
            bookId.toString(),
            "for user:",
            username);
  }

  @Test
  @DisplayName("removeFromOrder handles non-existent book")
  void shouldHandleNonExistentBook() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Long bookId = 1L;
    final Long copies = 2L;
    final Order existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final Order updatedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .set(field(Order::getLines), Instancio.ofList(OrderItem.class).size(1).create())
            .create();

    when(RemoveFromOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(username);
    when(RemoveFromOrderUseCaseImplTest.this.currentOrderService.getOrCreateOrder(username))
        .thenReturn(existingOrder);
    when(RemoveFromOrderUseCaseImplTest.this.orderRepository.deleteOrderItem(
            existingOrder.getId(), bookId, copies))
        .thenReturn(updatedOrder);

    // WHEN
    final Optional<Order> result =
        RemoveFromOrderUseCaseImplTest.this.removeFromOrderUseCase.removeFromOrder(bookId, copies);

    // THEN
    assertThat(result).isPresent().contains(updatedOrder);
    verify(RemoveFromOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(RemoveFromOrderUseCaseImplTest.this.currentOrderService).getOrCreateOrder(username);
    verify(RemoveFromOrderUseCaseImplTest.this.orderRepository)
        .deleteOrderItem(existingOrder.getId(), bookId, copies);
    verify(RemoveFromOrderUseCaseImplTest.this.finalPriceService).calculate(updatedOrder);
    verifyNoMoreInteractions(
        RemoveFromOrderUseCaseImplTest.this.securityService,
        RemoveFromOrderUseCaseImplTest.this.currentOrderService,
        RemoveFromOrderUseCaseImplTest.this.orderRepository,
        RemoveFromOrderUseCaseImplTest.this.finalPriceService);
    verifyNoInteractions(RemoveFromOrderUseCaseImplTest.this.availableBookCopiesService);

    final List<String> infoLogs = RemoveFromOrderUseCaseImplTest.this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains(
            "Removing",
            copies.toString(),
            "copies of bookId",
            bookId.toString(),
            "for user:",
            username);
  }

  @Test
  @DisplayName("removeFromOrder propagates repository exception")
  void shouldPropagateException_whenRepositoryFails() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Long bookId = 1L;
    final Long copies = 2L;
    final Order existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final RuntimeException exception = new RuntimeException("Repository error");

    when(RemoveFromOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(username);
    when(RemoveFromOrderUseCaseImplTest.this.currentOrderService.getOrCreateOrder(username))
        .thenReturn(existingOrder);
    when(RemoveFromOrderUseCaseImplTest.this.orderRepository.deleteOrderItem(
            existingOrder.getId(), bookId, copies))
        .thenThrow(exception);

    // WHEN / THEN
    assertThatThrownBy(
            () ->
                RemoveFromOrderUseCaseImplTest.this.removeFromOrderUseCase.removeFromOrder(
                    bookId, copies))
        .isEqualTo(exception);

    verify(RemoveFromOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(RemoveFromOrderUseCaseImplTest.this.currentOrderService).getOrCreateOrder(username);
    verify(RemoveFromOrderUseCaseImplTest.this.orderRepository)
        .deleteOrderItem(existingOrder.getId(), bookId, copies);
    verifyNoMoreInteractions(
        RemoveFromOrderUseCaseImplTest.this.securityService,
        RemoveFromOrderUseCaseImplTest.this.currentOrderService,
        RemoveFromOrderUseCaseImplTest.this.orderRepository);
    verifyNoInteractions(
        RemoveFromOrderUseCaseImplTest.this.finalPriceService,
        RemoveFromOrderUseCaseImplTest.this.availableBookCopiesService);

    final List<String> infoLogs = RemoveFromOrderUseCaseImplTest.this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains(
            "Removing",
            copies.toString(),
            "copies of bookId",
            bookId.toString(),
            "for user:",
            username);
  }

  @Test
  @DisplayName("removeFromOrder propagates final price service exception")
  void shouldPropagateException_whenFinalPriceServiceFails() {
    // GIVEN
    final String username = Instancio.create(String.class);
    final Long bookId = 1L;
    final Long copies = 2L;
    final Order existingOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .create();
    final Order updatedOrder =
        Instancio.of(Order.class)
            .generate(field(Order::getId), gen -> gen.longs().min(1L))
            .set(field(Order::getLines), Instancio.ofList(OrderItem.class).size(1).create())
            .create();
    final RuntimeException exception = new RuntimeException("Price calculation error");

    when(RemoveFromOrderUseCaseImplTest.this.securityService.getCurrentUserName())
        .thenReturn(username);
    when(RemoveFromOrderUseCaseImplTest.this.currentOrderService.getOrCreateOrder(username))
        .thenReturn(existingOrder);
    when(RemoveFromOrderUseCaseImplTest.this.orderRepository.deleteOrderItem(
            existingOrder.getId(), bookId, copies))
        .thenReturn(updatedOrder);
    doThrow(exception)
        .when(RemoveFromOrderUseCaseImplTest.this.finalPriceService)
        .calculate(updatedOrder);

    // WHEN / THEN
    assertThatThrownBy(
            () ->
                RemoveFromOrderUseCaseImplTest.this.removeFromOrderUseCase.removeFromOrder(
                    bookId, copies))
        .isEqualTo(exception);

    verify(RemoveFromOrderUseCaseImplTest.this.securityService).getCurrentUserName();
    verify(RemoveFromOrderUseCaseImplTest.this.currentOrderService).getOrCreateOrder(username);
    verify(RemoveFromOrderUseCaseImplTest.this.orderRepository)
        .deleteOrderItem(existingOrder.getId(), bookId, copies);
    verify(RemoveFromOrderUseCaseImplTest.this.finalPriceService).calculate(updatedOrder);
    verifyNoMoreInteractions(
        RemoveFromOrderUseCaseImplTest.this.securityService,
        RemoveFromOrderUseCaseImplTest.this.currentOrderService,
        RemoveFromOrderUseCaseImplTest.this.orderRepository,
        RemoveFromOrderUseCaseImplTest.this.finalPriceService);
    verifyNoInteractions(RemoveFromOrderUseCaseImplTest.this.availableBookCopiesService);

    final List<String> infoLogs = RemoveFromOrderUseCaseImplTest.this.logCaptor.getInfoLogs();
    assertThat(infoLogs).hasSize(1);
    assertThat(infoLogs.getFirst())
        .contains(
            "Removing",
            copies.toString(),
            "copies of bookId",
            bookId.toString(),
            "for user:",
            username);
  }
}
