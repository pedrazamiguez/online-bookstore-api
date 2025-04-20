package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import lombok.Data;

@Data
public class BookAllocation {

  private Long id;
  private Book book;
  private Long copies;
}
