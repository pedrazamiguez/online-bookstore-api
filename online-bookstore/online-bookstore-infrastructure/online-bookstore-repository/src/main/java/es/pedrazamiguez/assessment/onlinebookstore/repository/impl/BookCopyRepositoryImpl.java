package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.dto.InventoryDetailsDto;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookCopyEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookCopyJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.BookCopyEntityMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
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

    final List<BookCopyEntity> bookCopiesToSave =
        this.bookCopyEntityMapper.toEntityList(this.getBookEntity(bookId), copies);
    this.bookCopyJpaRepository.saveAll(bookCopiesToSave);
  }

  @Override
  public void deleteCopies(final Long bookId, final Long copies) {
    log.info("Deleting {} copies of book with ID {}", copies, bookId);
    this.bookCopyJpaRepository.deleteByBookIdAndCopies(bookId, copies);
  }

  @Override
  public void deleteCopiesOlderThan(final LocalDateTime olderThan) {
    final Timestamp timestamp = this.bookCopyEntityMapper.toTimestamp(olderThan);
    log.info("Deleting copies older than {}", timestamp);
    this.bookCopyJpaRepository.deleteByUpdatedAtBefore(timestamp);
  }

  @Override
  public List<BookAllocation> getInventoryDetails(final boolean includeOutOfStock) {
    final List<InventoryDetailsDto> inventoryDetailsDtoList =
        this.bookCopyJpaRepository.findInventoryDetails(includeOutOfStock ? 0 : 1);
    return this.bookCopyEntityMapper.toDomainList(inventoryDetailsDtoList);
  }

  @Override
  public Optional<BookAllocation> getInventoryDetailsByBookId(final Long bookId) {
    log.info("Fetching inventory details for book with ID {}", bookId);
    final InventoryDetailsDto inventoryDetailsDto =
        this.bookCopyJpaRepository.findInventoryDetailsForBook(bookId);

    if (ObjectUtils.isEmpty(inventoryDetailsDto)) {
      log.warn("No inventory details found for book with ID {}", bookId);
      return Optional.empty();
    }

    return Optional.of(
        this.bookCopyEntityMapper.inventoryDetailsDtoToInventoryDetails(inventoryDetailsDto));
  }

  private BookEntity getBookEntity(final Long bookId) {
    return this.bookJpaRepository
        .findById(bookId)
        .orElseThrow(() -> new BookNotFoundException(bookId));
  }
}
