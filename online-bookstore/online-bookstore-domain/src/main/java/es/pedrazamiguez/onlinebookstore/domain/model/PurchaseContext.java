package es.pedrazamiguez.onlinebookstore.domain.model;

import es.pedrazamiguez.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.onlinebookstore.domain.enums.PurchaseStatus;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PurchaseContext {
  private Order existingOrder;
  private Order purchasedOrder;
  private String username;
  private PurchaseStatus status = PurchaseStatus.PENDING;
  private String errorMessage;
  private BigDecimal totalAmount;
  private boolean orderPlaced;
  private PaymentMethod paymentMethod;
  private boolean paymentProcessed;
  private String shippingAddress;
  private boolean shippingPrepared;
  private Long loyaltyPointsEarned;

  public boolean isSuccessful() {
    return this.status == PurchaseStatus.SUCCESS || this.status == PurchaseStatus.PENDING;
  }
}
