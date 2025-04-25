package es.pedrazamiguez.assessment.onlinebookstore.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

@Data
@EqualsAndHashCode(callSuper = true)
@Audited
@Entity
@Table(
    name = "customers",
    indexes = {
      @Index(name = "idx_customers_username", columnList = "username"),
      @Index(name = "idx_customers_email", columnList = "email"),
    })
public class CustomerEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Column private String address;

  @Column private String phone;
}
