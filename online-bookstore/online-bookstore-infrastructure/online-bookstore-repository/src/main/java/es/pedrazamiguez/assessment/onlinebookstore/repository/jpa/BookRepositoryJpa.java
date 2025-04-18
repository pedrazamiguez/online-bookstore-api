package es.pedrazamiguez.assessment.onlinebookstore.repository.jpa;

import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepositoryJpa extends JpaRepository<BookEntity, Long> {}
