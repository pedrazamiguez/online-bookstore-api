package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookAllocation {

  private Long id;
  private Book book;
  private Long copies;
  private LocalDateTime lastUpdatedAt;
  private String lastUpdatedBy;
}
