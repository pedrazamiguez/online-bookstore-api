package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.envers.Audited;

@Data
@Entity
@Audited
@Table(name = "book_types")
public class BookTypeEntity {
  @Id
  @Column(nullable = false, updatable = false)
  private String code;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;
}
