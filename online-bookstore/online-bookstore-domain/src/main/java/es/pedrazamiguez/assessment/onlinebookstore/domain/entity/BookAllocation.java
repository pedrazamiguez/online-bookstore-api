package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookAllocation {

  private Long id;
  private Book book;
  private Long copies;
  private BigDecimal discount;
  private BigDecimal subtotal;
}
