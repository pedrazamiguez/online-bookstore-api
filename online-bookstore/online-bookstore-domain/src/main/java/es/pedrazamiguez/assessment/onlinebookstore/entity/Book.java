package es.pedrazamiguez.assessment.onlinebookstore.entity;

import es.pedrazamiguez.assessment.onlinebookstore.enums.Genre;
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
