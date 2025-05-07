package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

import es.pedrazamiguez.assessment.onlinebookstore.application.coordinator.PurchaseChainCoordinator;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.OrderStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PurchaseStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.loyalty.GetLoyaltyPointsUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.order.AddToOrderUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.purchase.PerformPurchaseUseCase;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.OrderJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.utils.TestSecurityUtils;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class PerformPurchaseUseCaseImplIT {

  private final ArgumentCaptor<PurchaseContext> purchaseContextArgumentCaptor =
      ArgumentCaptor.forClass(PurchaseContext.class);

  @Autowired private AddToOrderUseCase addToOrderUseCase;
  @Autowired private PerformPurchaseUseCase performPurchaseUseCase;
  @MockitoSpyBean private PurchaseChainCoordinator purchaseChainCoordinator;
  @Autowired private OrderJpaRepository orderJpaRepository;
  @Autowired private GetLoyaltyPointsUseCase getLoyaltyPointsUseCase;

  @AfterEach
  void tearDown() {
    TestSecurityUtils.clearAuthentication();
  }

  @Test
  @DisplayName(
      """
       Given
         - an authenticated user
         - a valid order with items in the cart
         - a valid order request with payment method and shipping address
       When the purchase is performed
       Then
         - the order is placed successfully
         - inventory is updated
         - loyalty points are awarded
      """)
  void givenValidOrderRequest_whenPurchase_thenOrderIsPlaced() {
    // GIVEN
    TestSecurityUtils.setAuthenticatedUser("bob", "USER");

    final var lines = new HashMap<Long, Long>();
    lines.put(1L, 2L);
    lines.put(51L, 3L);
    lines.put(101L, 1L);
    lines.forEach((bookId, copies) -> this.addToOrderUseCase.addToOrder(bookId, copies));

    final var orderRequest = new Order();
    orderRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    orderRequest.setShippingAddress("123 Main St, Springfield, USA");

    // WHEN
    this.performPurchaseUseCase.purchase(orderRequest);

    // THEN
    verify(this.purchaseChainCoordinator)
        .getPurchasedOrder(this.purchaseContextArgumentCaptor.capture());

    final PurchaseContext purchaseContext = this.purchaseContextArgumentCaptor.getValue();
    final var purchasedOrderId = purchaseContext.getPurchasedOrder().getId();
    final OrderEntity purchasedOrder =
        this.orderJpaRepository
            .findById(purchasedOrderId)
            .orElseThrow(() -> new OrderNotFoundException(purchasedOrderId));
    final var loyaltyPoints = this.getLoyaltyPointsUseCase.getCurrentCustomerLoyaltyPoints();

    this.assertPurchaseCompletedSuccessfully(purchaseContext, purchasedOrder, loyaltyPoints);
  }

  private void assertPurchaseCompletedSuccessfully(
      final PurchaseContext purchaseContext,
      final OrderEntity purchasedOrder,
      final Long loyaltyPoints) {

    assertAll(
        "PurchaseContext assertions",
        () -> assertThat(purchaseContext.getStatus()).isEqualTo(PurchaseStatus.SUCCESS),
        () -> assertThat(purchaseContext.getLoyaltyPointsEarned()).isEqualTo(loyaltyPoints),
        () -> assertThat(purchaseContext.getErrorMessage()).isNull(),
        () -> assertThat(purchaseContext.getPurchasedOrder()).isNotNull(),
        () ->
            assertThat(purchaseContext.getPurchasedOrder().getStatus())
                .isEqualTo(OrderStatus.PURCHASED),
        () ->
            assertThat(purchaseContext.getPurchasedOrder().getPaymentMethod())
                .isEqualTo(PaymentMethod.CREDIT_CARD),
        () ->
            assertThat(purchaseContext.getPurchasedOrder().getShippingAddress())
                .isEqualTo("123 Main St, Springfield, USA"));

    assertAll(
        "Purchased Order assertions",
        () -> assertThat(purchasedOrder.getStatus()).isEqualTo(OrderStatus.PURCHASED),
        () ->
            assertThat(purchasedOrder.getCustomer().getUsername())
                .isEqualTo(TestSecurityUtils.getAuthenticatedUserName()),
        () -> assertThat(purchasedOrder.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD),
        () ->
            assertThat(purchasedOrder.getShippingAddress())
                .isEqualTo("123 Main St, Springfield, USA"));

    // assertAll("Inventory updates", () -> ...)
  }
}
