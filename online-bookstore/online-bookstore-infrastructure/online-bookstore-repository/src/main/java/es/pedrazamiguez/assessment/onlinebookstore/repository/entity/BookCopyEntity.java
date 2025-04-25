package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table(name = "book_copies")
public class BookCopyEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "book_id")
  private BookEntity book;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookCopyStatus status;
}
