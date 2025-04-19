package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.Genre;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "books")
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

  @Column(name = "year_published", nullable = false)
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
