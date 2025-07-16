package es.pedrazamiguez.onlinebookstore.domain.model;

import es.pedrazamiguez.onlinebookstore.domain.enums.BookCopyStatus;
import lombok.Data;

@Data
public class BookAllocation {

  private Long id;
  private Book book;
  private Long copies;
  private BookCopyStatus status;
}
