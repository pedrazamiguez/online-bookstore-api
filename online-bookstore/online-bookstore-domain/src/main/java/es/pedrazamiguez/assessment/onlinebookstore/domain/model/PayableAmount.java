package es.pedrazamiguez.assessment.onlinebookstore.domain.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PayableAmount {
    private BigDecimal discount;
    private BigDecimal subtotal;
}
