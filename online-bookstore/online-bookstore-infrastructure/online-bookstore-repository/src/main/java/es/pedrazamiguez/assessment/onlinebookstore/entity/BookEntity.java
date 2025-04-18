package es.pedrazamiguez.assessment.onlinebookstore.entity;

import es.pedrazamiguez.assessment.onlinebookstore.enums.Genre;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "books")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true, nullable = false)
  private String isbn;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(nullable = false)
  private String publisher;

  @Column(name = "year_published", nullable = false)
  private Integer yearPublished;

  @Column(nullable = false, precision = 10, scale = 4)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;
}
