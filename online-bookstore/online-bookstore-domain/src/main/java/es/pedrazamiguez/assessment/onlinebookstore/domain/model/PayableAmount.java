package es.pedrazamiguez.assessment.onlinebookstore.domain.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PayableAmount {
  private BigDecimal discount;
  private BigDecimal subtotal;

  public BigDecimal getDiscountPercentage() {
    return BigDecimal.ONE.subtract(this.discount).multiply(BigDecimal.valueOf(100));
  }
}
