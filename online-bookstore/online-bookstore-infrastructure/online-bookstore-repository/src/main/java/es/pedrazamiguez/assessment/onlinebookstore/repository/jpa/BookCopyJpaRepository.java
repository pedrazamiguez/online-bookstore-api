package es.pedrazamiguez.api.onlinebookstore.repository.jpa;

import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookCopyEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.projection.InventoryDetailsQueryResult;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyJpaRepository extends JpaRepository<BookCopyEntity, Long> {

  @Query(
      value =
          """
          select
            count(bc.id) as copies,
            max(bc.updated_at) as last_updated_at,
            min(bc.updated_by) as last_updated_by,
            b.id as book_id,
            b.isbn,
            b.title,
            b.author,
            b.publisher,
            b.year_published,
            b.price,
            b.genre,
            b.type_code
          from
            book_copies bc
            right join books b
              on bc.book_id = b.id
              and bc.status in (:statuses)
          group by
            bc.book_id, bc.book_id, b.title, b.author
          having
            count(bc.id) >= :count
          order by
            1 desc
          """,
      nativeQuery = true)
  List<InventoryDetailsQueryResult> findInventoryDetailsAndStatusIn(
      int count, List<String> statuses);

  @Query(
      value =
          """
          select
            count(bc.id) as copies,
            max(bc.updated_at) as last_updated_at,
            min(bc.updated_by) as last_updated_by,
            b.id as book_id,
            b.isbn,
            b.title,
            b.author,
            b.publisher,
            b.year_published,
            b.price,
            b.genre,
            b.type_code
          from
            book_copies bc
            inner join books b on bc.book_id = b.id
          where
            b.id = :bookId
            and
            bc.status in (:statuses)
          group by
            bc.book_id, bc.book_id, b.title, b.author
          """,
      nativeQuery = true)
  InventoryDetailsQueryResult findInventoryDetailsForBookAndStatusIn(
      Long bookId, List<String> statuses);

  @Query(
      value =
          """
          select
            bc.*
          from
            book_copies bc
          where
            bc.book_id = :bookId
            and
            bc.status in (:statuses)
          order by
            bc.updated_at
          limit :copies
          """,
      nativeQuery = true)
  List<BookCopyEntity> findByBookIdAndStatusIn(Long bookId, Long copies, List<String> statuses);

  List<BookCopyEntity> findByUpdatedAtBeforeAndStatusIn(LocalDateTime date, List<String> statuses);
}
