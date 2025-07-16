package es.pedrazamiguez.api.onlinebookstore.repository.impl;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.BookCopyStatus;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.api.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookCopyEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.BookCopyJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.mapper.BookCopyEntityMapper;
import es.pedrazamiguez.api.onlinebookstore.repository.projection.InventoryDetailsQueryResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookCopyRepositoryImpl implements BookCopyRepository {

  private final BookJpaRepository bookJpaRepository;

  private final BookCopyJpaRepository bookCopyJpaRepository;

  private final BookCopyEntityMapper bookCopyEntityMapper;

  @Override
  public void addCopies(final Long bookId, final Long copies) {
    log.info("Adding {} copies of book with ID {}", copies, bookId);

    final BookEntity bookEntity = this.getBookEntity(bookId);
    final List<BookCopyEntity> bookCopiesToSave = this.createBookCopies(bookEntity, copies);

    this.bookCopyJpaRepository.saveAll(bookCopiesToSave);
  }

  @Override
  public void updateCopiesStatus(
      final Long bookId, final Long copies, final BookCopyStatus status) {

    log.info("Updating status of {} copies of book with ID {} to {}", copies, bookId, status);
    final List<String> statusesForInventory = this.getStatusesForInventory();

    final List<BookCopyEntity> bookCopiesToUpdate =
        this.bookCopyJpaRepository.findByBookIdAndStatusIn(bookId, copies, statusesForInventory);

    if (!CollectionUtils.isEmpty(bookCopiesToUpdate)) {
      this.patchWithStatus(bookCopiesToUpdate, status);
      this.bookCopyJpaRepository.saveAll(bookCopiesToUpdate);
    }
  }

  @Override
  public void deleteCopiesOlderThan(final LocalDateTime olderThan) {
    log.info("Deleting copies older than {}", olderThan);
    final List<String> statusesForDeletion = this.getStatusesForDeletion();

    final List<BookCopyEntity> bookCopiesToDelete =
        this.bookCopyJpaRepository.findByUpdatedAtBeforeAndStatusIn(olderThan, statusesForDeletion);

    if (!CollectionUtils.isEmpty(bookCopiesToDelete)) {
      this.patchWithStatus(bookCopiesToDelete, BookCopyStatus.DELETED);
      this.bookCopyJpaRepository.saveAll(bookCopiesToDelete);
    }
  }

  @Override
  public List<BookAllocation> getInventoryDetails(final boolean includeOutOfStock) {
    log.info("Fetching inventory details with includeOutOfStock = {}", includeOutOfStock);
    final int count = includeOutOfStock ? 0 : 1;
    final List<String> statusesForInventory = this.getStatusesForInventory();

    final List<InventoryDetailsQueryResult> inventoryDetailsQueryResultList =
        this.bookCopyJpaRepository.findInventoryDetailsAndStatusIn(count, statusesForInventory);

    return this.bookCopyEntityMapper.toDomainList(inventoryDetailsQueryResultList);
  }

  @Override
  public Optional<BookAllocation> getInventoryDetailsByBookId(final Long bookId) {
    log.info("Fetching inventory details for book with ID {}", bookId);
    final List<String> statusesForInventory = this.getStatusesForInventory();

    final InventoryDetailsQueryResult inventoryDetailsQueryResult =
        this.bookCopyJpaRepository.findInventoryDetailsForBookAndStatusIn(
            bookId, statusesForInventory);

    if (ObjectUtils.isEmpty(inventoryDetailsQueryResult)) {
      log.warn("No inventory details found for book with ID {}", bookId);
      return Optional.empty();
    }

    return Optional.of(
        this.bookCopyEntityMapper.inventoryDetailsDtoToInventoryDetails(
            inventoryDetailsQueryResult));
  }

  private BookEntity getBookEntity(final Long bookId) {
    return this.bookJpaRepository
        .findById(bookId)
        .orElseThrow(() -> new BookNotFoundException(bookId));
  }

  private List<String> getStatusesForDeletion() {
    return List.of(BookCopyStatus.AVAILABLE.name(), BookCopyStatus.RESERVED.name());
  }

  private List<String> getStatusesForInventory() {
    return List.of(BookCopyStatus.AVAILABLE.name(), BookCopyStatus.RETURNED.name());
  }

  private List<BookCopyEntity> createBookCopies(final BookEntity bookEntity, final Long copies) {
    return LongStream.range(0, copies)
        .mapToObj(
            i -> {
              final BookCopyEntity bookCopyEntity = new BookCopyEntity();
              bookCopyEntity.setBook(bookEntity);
              this.patchWithStatus(bookCopyEntity, BookCopyStatus.AVAILABLE);
              return bookCopyEntity;
            })
        .toList();
  }

  private void patchWithStatus(final BookCopyEntity bookCopyEntity, final BookCopyStatus status) {
    bookCopyEntity.setStatus(status);
  }

  private void patchWithStatus(
      final List<BookCopyEntity> bookCopyEntities, final BookCopyStatus status) {
    bookCopyEntities.forEach(bookCopyEntity -> this.patchWithStatus(bookCopyEntity, status));
  }
}
