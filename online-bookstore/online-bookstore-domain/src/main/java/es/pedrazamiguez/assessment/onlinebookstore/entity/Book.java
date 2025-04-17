package es.pedrazamiguez.assessment.onlinebookstore.entity;

import es.pedrazamiguez.assessment.onlinebookstore.enums.Genre;
import lombok.Data;

import java.math.BigDecimal;

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
