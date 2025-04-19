package es.pedrazamiguez.assessment.onlinebookstore.repository.jpa;

import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyJpaRepository extends JpaRepository<BookCopyEntity, Long> {}
