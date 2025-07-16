package es.pedrazamiguez.api.onlinebookstore.repository.jpa;

import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, Long> {}
