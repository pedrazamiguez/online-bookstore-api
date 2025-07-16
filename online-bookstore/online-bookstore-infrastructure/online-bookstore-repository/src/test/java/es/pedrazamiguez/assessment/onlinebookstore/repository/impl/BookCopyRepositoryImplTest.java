package es.pedrazamiguez.api.onlinebookstore.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.BookCopyStatus;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.BookNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookCopyEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.BookEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.BookCopyJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.BookJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.mapper.BookCopyEntityMapper;
import es.pedrazamiguez.api.onlinebookstore.repository.projection.InventoryDetailsQueryResult;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookCopyRepositoryImplTest {

  @InjectMocks private BookCopyRepositoryImpl bookCopyRepository;

  @Mock private BookJpaRepository bookJpaRepository;

  @Mock private BookCopyJpaRepository bookCopyJpaRepository;

  @Mock private BookCopyEntityMapper bookCopyEntityMapper;

  @Captor private ArgumentCaptor<List<BookCopyEntity>> bookCopiesCaptor;

  private BookEntity bookEntity;
  private Long bookId;
  private List<String> statusesForInventory;
  private List<String> statusesForDeletion;

  @BeforeEach
  void setUp() {
    this.bookId = Instancio.create(Long.class);
    this.bookEntity = Instancio.create(BookEntity.class);
    this.statusesForInventory =
        List.of(BookCopyStatus.AVAILABLE.name(), BookCopyStatus.RETURNED.name());
    this.statusesForDeletion =
        List.of(BookCopyStatus.AVAILABLE.name(), BookCopyStatus.RESERVED.name());
  }

  @Nested
  @DisplayName("Tests for addCopies")
  class AddCopiesTests {

    @Test
    @DisplayName("addCopies adds book copies successfully")
    void shouldAddCopiesSuccessfully() {
      // GIVEN
      final Long copies = 5L;

      when(BookCopyRepositoryImplTest.this.bookJpaRepository.findById(
              BookCopyRepositoryImplTest.this.bookId))
          .thenReturn(Optional.of(BookCopyRepositoryImplTest.this.bookEntity));

      // WHEN
      BookCopyRepositoryImplTest.this.bookCopyRepository.addCopies(
          BookCopyRepositoryImplTest.this.bookId, copies);

      // THEN
      verify(BookCopyRepositoryImplTest.this.bookJpaRepository)
          .findById(BookCopyRepositoryImplTest.this.bookId);
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .saveAll(BookCopyRepositoryImplTest.this.bookCopiesCaptor.capture());

      final List<BookCopyEntity> savedCopies =
          BookCopyRepositoryImplTest.this.bookCopiesCaptor.getValue();
      assertThat(savedCopies).hasSize(5);
      savedCopies.forEach(
          copy -> {
            assertThat(copy.getBook()).isEqualTo(BookCopyRepositoryImplTest.this.bookEntity);
            assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
          });

      verifyNoMoreInteractions(
          BookCopyRepositoryImplTest.this.bookJpaRepository,
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository);
    }

    @Test
    @DisplayName("addCopies throws BookNotFoundException when book does not exist")
    void shouldThrowBookNotFoundException_whenBookDoesNotExist() {
      // GIVEN
      final Long copies = 5L;
      when(BookCopyRepositoryImplTest.this.bookJpaRepository.findById(
              BookCopyRepositoryImplTest.this.bookId))
          .thenReturn(Optional.empty());

      // WHEN
      assertThatThrownBy(
              () ->
                  BookCopyRepositoryImplTest.this.bookCopyRepository.addCopies(
                      BookCopyRepositoryImplTest.this.bookId, copies))
          .isInstanceOf(BookNotFoundException.class)
          .hasMessageContaining(BookCopyRepositoryImplTest.this.bookId.toString());

      // THEN
      verify(BookCopyRepositoryImplTest.this.bookJpaRepository)
          .findById(BookCopyRepositoryImplTest.this.bookId);
      verifyNoInteractions(
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper,
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository);
    }
  }

  @Nested
  @DisplayName("Tests for updateCopiesStatus")
  class UpdateCopiesStatusTests {

    @Test
    @DisplayName("updateCopiesStatus updates book copies status successfully")
    void shouldUpdateCopiesStatusSuccessfully() {
      // GIVEN
      final Long copies = 3L;
      final BookCopyStatus status = BookCopyStatus.RESERVED;
      final List<BookCopyEntity> bookCopies =
          Instancio.ofList(BookCopyEntity.class).size(3).create();

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.findByBookIdAndStatusIn(
              BookCopyRepositoryImplTest.this.bookId,
              copies,
              BookCopyRepositoryImplTest.this.statusesForInventory))
          .thenReturn(bookCopies);
      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.saveAll(bookCopies))
          .thenReturn(bookCopies);

      // WHEN
      BookCopyRepositoryImplTest.this.bookCopyRepository.updateCopiesStatus(
          BookCopyRepositoryImplTest.this.bookId, copies, status);

      // THEN
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findByBookIdAndStatusIn(
              BookCopyRepositoryImplTest.this.bookId,
              copies,
              BookCopyRepositoryImplTest.this.statusesForInventory);
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository).saveAll(bookCopies);
      verifyNoMoreInteractions(
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository,
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper);
      verifyNoInteractions(BookCopyRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("updateCopiesStatus does nothing when no copies are found")
    void shouldDoNothing_whenNoCopiesFound() {
      // GIVEN
      final Long copies = 3L;
      final BookCopyStatus status = BookCopyStatus.RESERVED;

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.findByBookIdAndStatusIn(
              BookCopyRepositoryImplTest.this.bookId,
              copies,
              BookCopyRepositoryImplTest.this.statusesForInventory))
          .thenReturn(Collections.emptyList());

      // WHEN
      BookCopyRepositoryImplTest.this.bookCopyRepository.updateCopiesStatus(
          BookCopyRepositoryImplTest.this.bookId, copies, status);

      // THEN
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findByBookIdAndStatusIn(
              BookCopyRepositoryImplTest.this.bookId,
              copies,
              BookCopyRepositoryImplTest.this.statusesForInventory);
      verifyNoInteractions(
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper,
          BookCopyRepositoryImplTest.this.bookJpaRepository);
      verifyNoMoreInteractions(BookCopyRepositoryImplTest.this.bookCopyJpaRepository);
    }
  }

  @Nested
  @DisplayName("Tests for deleteCopiesOlderThan")
  class DeleteCopiesOlderThanTests {

    @Test
    @DisplayName("deleteCopiesOlderThan deletes old copies successfully")
    void shouldDeleteOldCopiesSuccessfully() {
      // GIVEN
      final LocalDateTime olderThan = LocalDateTime.now().minusDays(30);
      final List<BookCopyEntity> bookCopies =
          Instancio.ofList(BookCopyEntity.class).size(10).create();

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.findByUpdatedAtBeforeAndStatusIn(
              olderThan, BookCopyRepositoryImplTest.this.statusesForDeletion))
          .thenReturn(bookCopies);
      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.saveAll(bookCopies))
          .thenReturn(bookCopies);

      // WHEN
      BookCopyRepositoryImplTest.this.bookCopyRepository.deleteCopiesOlderThan(olderThan);

      // THEN
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findByUpdatedAtBeforeAndStatusIn(
              olderThan, BookCopyRepositoryImplTest.this.statusesForDeletion);
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository).saveAll(bookCopies);
      verifyNoMoreInteractions(
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository,
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper);
      verifyNoInteractions(BookCopyRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("deleteCopiesOlderThan does nothing when no old copies are found")
    void shouldDoNothing_whenNoOldCopiesFound() {
      // GIVEN
      final LocalDateTime olderThan = LocalDateTime.now().minusDays(30);

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.findByUpdatedAtBeforeAndStatusIn(
              olderThan, BookCopyRepositoryImplTest.this.statusesForDeletion))
          .thenReturn(Collections.emptyList());

      // WHEN
      BookCopyRepositoryImplTest.this.bookCopyRepository.deleteCopiesOlderThan(olderThan);

      // THEN
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findByUpdatedAtBeforeAndStatusIn(
              olderThan, BookCopyRepositoryImplTest.this.statusesForDeletion);
      verifyNoInteractions(
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper,
          BookCopyRepositoryImplTest.this.bookJpaRepository);
      verifyNoMoreInteractions(BookCopyRepositoryImplTest.this.bookCopyJpaRepository);
    }
  }

  @Nested
  @DisplayName("Tests for getInventoryDetails")
  class GetInventoryDetailsTests {

    @Test
    @DisplayName("getInventoryDetails returns inventory details with includeOutOfStock true")
    void shouldReturnInventoryDetails_whenIncludeOutOfStockTrue() {
      // GIVEN
      final boolean includeOutOfStock = true;
      final List<InventoryDetailsQueryResult> queryResults =
          Instancio.ofList(InventoryDetailsQueryResult.class).size(5).create();
      final List<BookAllocation> bookAllocations =
          Instancio.ofList(BookAllocation.class).size(5).create();

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.findInventoryDetailsAndStatusIn(
              0, BookCopyRepositoryImplTest.this.statusesForInventory))
          .thenReturn(queryResults);
      when(BookCopyRepositoryImplTest.this.bookCopyEntityMapper.toDomainList(queryResults))
          .thenReturn(bookAllocations);

      // WHEN
      final List<BookAllocation> result =
          BookCopyRepositoryImplTest.this.bookCopyRepository.getInventoryDetails(includeOutOfStock);

      // THEN
      assertThat(result).isEqualTo(bookAllocations);
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findInventoryDetailsAndStatusIn(0, BookCopyRepositoryImplTest.this.statusesForInventory);
      verify(BookCopyRepositoryImplTest.this.bookCopyEntityMapper).toDomainList(queryResults);
      verifyNoMoreInteractions(
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository,
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper);
      verifyNoInteractions(BookCopyRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("getInventoryDetails returns inventory details with includeOutOfStock false")
    void shouldReturnInventoryDetails_whenIncludeOutOfStockFalse() {
      // GIVEN
      final boolean includeOutOfStock = false;
      final List<InventoryDetailsQueryResult> queryResults =
          Instancio.ofList(InventoryDetailsQueryResult.class).size(5).create();
      final List<BookAllocation> bookAllocations =
          Instancio.ofList(BookAllocation.class).size(5).create();

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.findInventoryDetailsAndStatusIn(
              1, BookCopyRepositoryImplTest.this.statusesForInventory))
          .thenReturn(queryResults);
      when(BookCopyRepositoryImplTest.this.bookCopyEntityMapper.toDomainList(queryResults))
          .thenReturn(bookAllocations);

      // WHEN
      final List<BookAllocation> result =
          BookCopyRepositoryImplTest.this.bookCopyRepository.getInventoryDetails(includeOutOfStock);

      // THEN
      assertThat(result).isEqualTo(bookAllocations);
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findInventoryDetailsAndStatusIn(1, BookCopyRepositoryImplTest.this.statusesForInventory);
      verify(BookCopyRepositoryImplTest.this.bookCopyEntityMapper).toDomainList(queryResults);
      verifyNoMoreInteractions(
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository,
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper);
      verifyNoInteractions(BookCopyRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("getInventoryDetails returns empty list when no inventory details found")
    void shouldReturnEmptyList_whenNoInventoryDetailsFound() {
      // GIVEN
      final boolean includeOutOfStock = true;

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository.findInventoryDetailsAndStatusIn(
              0, BookCopyRepositoryImplTest.this.statusesForInventory))
          .thenReturn(Collections.emptyList());
      when(BookCopyRepositoryImplTest.this.bookCopyEntityMapper.toDomainList(
              Collections.emptyList()))
          .thenReturn(Collections.emptyList());

      // WHEN
      final List<BookAllocation> result =
          BookCopyRepositoryImplTest.this.bookCopyRepository.getInventoryDetails(includeOutOfStock);

      // THEN
      assertThat(result).isEmpty();
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findInventoryDetailsAndStatusIn(0, BookCopyRepositoryImplTest.this.statusesForInventory);
      verify(BookCopyRepositoryImplTest.this.bookCopyEntityMapper)
          .toDomainList(Collections.emptyList());
      verifyNoMoreInteractions(
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository,
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper);
      verifyNoInteractions(BookCopyRepositoryImplTest.this.bookJpaRepository);
    }
  }

  @Nested
  @DisplayName("Tests for getInventoryDetailsByBookId")
  class GetInventoryDetailsByBookIdTests {

    @Test
    @DisplayName("getInventoryDetailsByBookId returns inventory details for book")
    void shouldReturnInventoryDetailsForBook() {
      // GIVEN
      final InventoryDetailsQueryResult queryResult =
          Instancio.create(InventoryDetailsQueryResult.class);
      final BookAllocation bookAllocation = Instancio.create(BookAllocation.class);

      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository
              .findInventoryDetailsForBookAndStatusIn(
                  BookCopyRepositoryImplTest.this.bookId,
                  BookCopyRepositoryImplTest.this.statusesForInventory))
          .thenReturn(queryResult);
      when(BookCopyRepositoryImplTest.this.bookCopyEntityMapper
              .inventoryDetailsDtoToInventoryDetails(queryResult))
          .thenReturn(bookAllocation);

      // WHEN
      final Optional<BookAllocation> result =
          BookCopyRepositoryImplTest.this.bookCopyRepository.getInventoryDetailsByBookId(
              BookCopyRepositoryImplTest.this.bookId);

      // THEN
      assertThat(result).isPresent().contains(bookAllocation);
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findInventoryDetailsForBookAndStatusIn(
              BookCopyRepositoryImplTest.this.bookId,
              BookCopyRepositoryImplTest.this.statusesForInventory);
      verify(BookCopyRepositoryImplTest.this.bookCopyEntityMapper)
          .inventoryDetailsDtoToInventoryDetails(queryResult);
      verifyNoMoreInteractions(
          BookCopyRepositoryImplTest.this.bookCopyJpaRepository,
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper);
      verifyNoInteractions(BookCopyRepositoryImplTest.this.bookJpaRepository);
    }

    @Test
    @DisplayName("getInventoryDetailsByBookId returns empty when no inventory details found")
    void shouldReturnEmpty_whenNoInventoryDetailsFound() {
      // GIVEN
      when(BookCopyRepositoryImplTest.this.bookCopyJpaRepository
              .findInventoryDetailsForBookAndStatusIn(
                  BookCopyRepositoryImplTest.this.bookId,
                  BookCopyRepositoryImplTest.this.statusesForInventory))
          .thenReturn(null);

      // WHEN
      final Optional<BookAllocation> result =
          BookCopyRepositoryImplTest.this.bookCopyRepository.getInventoryDetailsByBookId(
              BookCopyRepositoryImplTest.this.bookId);

      // THEN
      assertThat(result).isEmpty();
      verify(BookCopyRepositoryImplTest.this.bookCopyJpaRepository)
          .findInventoryDetailsForBookAndStatusIn(
              BookCopyRepositoryImplTest.this.bookId,
              BookCopyRepositoryImplTest.this.statusesForInventory);
      verifyNoInteractions(
          BookCopyRepositoryImplTest.this.bookCopyEntityMapper,
          BookCopyRepositoryImplTest.this.bookJpaRepository);
      verifyNoMoreInteractions(BookCopyRepositoryImplTest.this.bookCopyJpaRepository);
    }
  }
}
