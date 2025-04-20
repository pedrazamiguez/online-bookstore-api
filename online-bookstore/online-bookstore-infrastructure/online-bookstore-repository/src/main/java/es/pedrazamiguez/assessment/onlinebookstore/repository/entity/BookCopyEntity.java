package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

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
}
