package es.pedrazamiguez.onlinebookstore.domain.model;

import es.pedrazamiguez.onlinebookstore.domain.enums.Genre;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Book {
  private Long id;
  private String isbn;
  private String title;
  private String author;
  private String publisher;
  private Integer yearPublished;
  private BigDecimal price;
  private Genre genre;
  private BookType type;
}
