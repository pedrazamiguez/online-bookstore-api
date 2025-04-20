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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookCopyRepositoryImpl implements BookCopyRepository {

  private final BookJpaRepository bookJpaRepository;

  private final BookCopyJpaRepository bookCopyJpaRepository;

  private final BookCopyEntityMapper bookCopyEntityMapper;

  @Override
  public void addCopies(final Long bookId, final int copies) {
    log.info("Adding {} copies of book with ID {}", copies, bookId);

    final List<BookCopyEntity> bookCopiesToSave =
        this.bookCopyEntityMapper.toEntityList(this.getBookEntity(bookId), copies);
    this.bookCopyJpaRepository.saveAll(bookCopiesToSave);
  }

  @Override
  public List<BookAllocation> getInventoryDetails(final boolean retrieveOutOfStock) {
    final List<InventoryDetailsDto> inventoryDetailsDtoList =
        this.bookCopyJpaRepository.findInventoryDetails(retrieveOutOfStock ? 0 : 1);
    return this.bookCopyEntityMapper.toDomainList(inventoryDetailsDtoList);
  }

  private BookEntity getBookEntity(final Long bookId) {
    return this.bookJpaRepository
        .findById(bookId)
        .orElseThrow(() -> new BookNotFoundException(bookId));
  }
}
