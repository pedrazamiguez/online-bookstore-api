package es.pedrazamiguez.api.onlinebookstore.repository.entity;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.Genre;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table(
    name = "books",
    indexes = {
      @Index(name = "idx_books_type_code", columnList = "type_code"),
      @Index(name = "idx_books_genre", columnList = "genre"),
    })
public class BookEntity extends AuditEntity {

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

  @Column(nullable = false)
  private Integer yearPublished;

  @Column(nullable = false, precision = 10, scale = 4)
  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  @ManyToOne(optional = false)
  @JoinColumn(name = "type_code", referencedColumnName = "code")
  private BookTypeEntity type;
}
