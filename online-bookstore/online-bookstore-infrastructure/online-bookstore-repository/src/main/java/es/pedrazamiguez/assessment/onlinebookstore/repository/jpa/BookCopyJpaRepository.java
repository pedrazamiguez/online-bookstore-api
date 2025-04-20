package es.pedrazamiguez.assessment.onlinebookstore.repository.jpa;

import es.pedrazamiguez.assessment.onlinebookstore.repository.projection.InventoryDetailsQueryResult;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookCopyEntity;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            right join books b on bc.book_id = b.id
          group by
            bc.book_id, bc.book_id, b.title, b.author
          having
            count(bc.id) >= :count
          order by
            1 desc
          """,
      nativeQuery = true)
  List<InventoryDetailsQueryResult> findInventoryDetails(int count);

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
          group by
            bc.book_id, bc.book_id, b.title, b.author
          """,
      nativeQuery = true)
  InventoryDetailsQueryResult findInventoryDetailsForBook(Long bookId);

  @Modifying
  @Query(
      value =
          """
          delete from
            book_copies
          where
            id in (
              select
                bc.id
              from
                book_copies bc
              where
                bc.book_id = :bookId
              order by
                bc.updated_at
              limit :copies
            )
          """,
      nativeQuery = true)
  void deleteByBookIdAndCopies(Long bookId, Long copies);

  @Modifying
  @Query(
      value =
          """
          delete from
            book_copies
          where
            updated_at < :date
          """,
      nativeQuery = true)
  void deleteByUpdatedAtBefore(Timestamp date);
}
