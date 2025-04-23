package es.pedrazamiguez.assessment.onlinebookstore.domain.model;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PurchaseStatus;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PurchaseContext {
  private Order order;
  private String userId;
  private PurchaseStatus status = PurchaseStatus.PENDING;
  private String errorMessage;
  private BigDecimal totalAmount;
  private boolean paymentProcessed;
  private boolean shippingPrepared;
  private int loyaltyPointsEarned;

  public boolean isSuccessful() {
    return this.status == PurchaseStatus.SUCCESS || this.status == PurchaseStatus.PENDING;
  }
}
