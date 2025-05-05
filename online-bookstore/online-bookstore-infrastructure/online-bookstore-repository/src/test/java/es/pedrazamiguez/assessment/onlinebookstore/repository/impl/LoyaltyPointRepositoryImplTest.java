package es.pedrazamiguez.assessment.onlinebookstore.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.LoyaltyPointStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.LoyaltyPointEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.LoyaltyPointJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.jpa.OrderJpaRepository;
import es.pedrazamiguez.assessment.onlinebookstore.repository.mapper.LoyaltyPointMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import nl.altindag.log.LogCaptor;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoyaltyPointRepositoryImplTest {

  @InjectMocks private LoyaltyPointRepositoryImpl loyaltyPointRepository;

  @Mock private CustomerJpaRepository customerJpaRepository;

  @Mock private OrderJpaRepository orderJpaRepository;

  @Mock private LoyaltyPointJpaRepository loyaltyPointJpaRepository;

  @Mock private LoyaltyPointMapper loyaltyPointMapper;

  private LogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    this.logCaptor = LogCaptor.forClass(LoyaltyPointRepositoryImpl.class);
  }

  @AfterEach
  void tearDown() {
    this.logCaptor.close();
  }

  @Nested
  @DisplayName("Tests for addLoyaltyPoints")
  class AddLoyaltyPointsTests {

    @Test
    @DisplayName("addLoyaltyPoints saves loyalty points successfully")
    void shouldSaveLoyaltyPoints_whenCustomerAndOrderExist() {
      // GIVEN
      final String username = "testuser";
      final Long orderId = 1L;
      final Long points = 100L;
      final CustomerEntity customer = Instancio.create(CustomerEntity.class);
      final OrderEntity order = Instancio.create(OrderEntity.class);
      final List<LoyaltyPointEntity> loyaltyPoints =
          Instancio.ofList(LoyaltyPointEntity.class).size(2).create();

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.of(customer));
      when(LoyaltyPointRepositoryImplTest.this.orderJpaRepository.findById(orderId))
          .thenReturn(Optional.of(order));
      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper.toEarnedLoyaltyPoints(
              customer, order, points))
          .thenReturn(loyaltyPoints);
      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository.saveAll(loyaltyPoints))
          .thenReturn(loyaltyPoints);

      // WHEN
      LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
          username, orderId, points);

      // THEN
      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verify(LoyaltyPointRepositoryImplTest.this.orderJpaRepository).findById(orderId);
      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper)
          .toEarnedLoyaltyPoints(customer, order, points);
      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository).saveAll(loyaltyPoints);
      verifyNoMoreInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints throws CustomerNotFoundException when customer does not exist")
    void shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
      // GIVEN
      final String username = "testuser";
      final Long orderId = 1L;
      final Long points = 100L;

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.empty());

      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
                      username, orderId, points))
          .isInstanceOf(CustomerNotFoundException.class)
          .hasMessageContaining(username);

      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verifyNoMoreInteractions(LoyaltyPointRepositoryImplTest.this.customerJpaRepository);
      verifyNoInteractions(
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints throws OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundException_whenOrderNotFound() {
      // GIVEN
      final String username = "testuser";
      final Long orderId = 1L;
      final Long points = 100L;
      final CustomerEntity customer = Instancio.create(CustomerEntity.class);

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.of(customer));
      when(LoyaltyPointRepositoryImplTest.this.orderJpaRepository.findById(orderId))
          .thenReturn(Optional.empty());

      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
                      username, orderId, points))
          .isInstanceOf(OrderNotFoundException.class)
          .hasMessageContaining(orderId.toString());

      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verify(LoyaltyPointRepositoryImplTest.this.orderJpaRepository).findById(orderId);
      verifyNoMoreInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository);
      verifyNoInteractions(
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints handles zero points correctly")
    void shouldHandleZeroPoints_whenAddingLoyaltyPoints() {
      // GIVEN
      final String username = "testuser";
      final Long orderId = 1L;
      final Long points = 0L;
      final CustomerEntity customer = Instancio.create(CustomerEntity.class);
      final OrderEntity order = Instancio.create(OrderEntity.class);
      final List<LoyaltyPointEntity> loyaltyPoints = Collections.emptyList();

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.of(customer));
      when(LoyaltyPointRepositoryImplTest.this.orderJpaRepository.findById(orderId))
          .thenReturn(Optional.of(order));
      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper.toEarnedLoyaltyPoints(
              customer, order, points))
          .thenReturn(loyaltyPoints);

      // WHEN
      LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
          username, orderId, points);

      // THEN
      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verify(LoyaltyPointRepositoryImplTest.this.orderJpaRepository).findById(orderId);
      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper)
          .toEarnedLoyaltyPoints(customer, order, points);
      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository).saveAll(loyaltyPoints);
      verifyNoMoreInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints handles empty loyalty points list from mapper")
    void shouldHandleEmptyLoyaltyPointsList_whenMapperReturnsEmpty() {
      // GIVEN
      final String username = "testuser";
      final Long orderId = 1L;
      final Long points = 100L;
      final CustomerEntity customer = Instancio.create(CustomerEntity.class);
      final OrderEntity order = Instancio.create(OrderEntity.class);
      final List<LoyaltyPointEntity> loyaltyPoints = Collections.emptyList();

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.of(customer));
      when(LoyaltyPointRepositoryImplTest.this.orderJpaRepository.findById(orderId))
          .thenReturn(Optional.of(order));
      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper.toEarnedLoyaltyPoints(
              customer, order, points))
          .thenReturn(loyaltyPoints);

      // WHEN
      LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
          username, orderId, points);

      // THEN
      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verify(LoyaltyPointRepositoryImplTest.this.orderJpaRepository).findById(orderId);
      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper)
          .toEarnedLoyaltyPoints(customer, order, points);
      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository).saveAll(loyaltyPoints);
      verifyNoMoreInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }
  }

  @Nested
  @DisplayName("Tests for getLoyaltyPoints")
  class GetLoyaltyPointsTests {

    @Test
    @DisplayName("getLoyaltyPoints returns count of earned points")
    void shouldReturnEarnedPoints_whenUserHasPoints() {
      // GIVEN
      final String username = "testuser";
      final Long points = 50L;

      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository
              .countLoyaltyPointsByCustomerUsernameAndStatusIn(
                  username, LoyaltyPointStatus.EARNED.name()))
          .thenReturn(points);

      // WHEN
      final Long result =
          LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.getLoyaltyPoints(username);

      // THEN
      assertThat(result).isEqualTo(points);

      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository)
          .countLoyaltyPointsByCustomerUsernameAndStatusIn(
              username, LoyaltyPointStatus.EARNED.name());
      verifyNoMoreInteractions(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);
      verifyNoInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst()).contains("Getting earned loyalty points for user:", username);
    }

    @Test
    @DisplayName("getLoyaltyPoints returns zero when user has no points")
    void shouldReturnZero_whenUserHasNoPoints() {
      // GIVEN
      final String username = "testuser";

      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository
              .countLoyaltyPointsByCustomerUsernameAndStatusIn(
                  username, LoyaltyPointStatus.EARNED.name()))
          .thenReturn(0L);

      // WHEN
      final Long result =
          LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.getLoyaltyPoints(username);

      // THEN
      assertThat(result).isZero();

      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository)
          .countLoyaltyPointsByCustomerUsernameAndStatusIn(
              username, LoyaltyPointStatus.EARNED.name());
      verifyNoMoreInteractions(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);
      verifyNoInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst()).contains("Getting earned loyalty points for user:", username);
    }

    @Test
    @DisplayName("getLoyaltyPoints propagates repository exception")
    void shouldPropagateException_whenRepositoryFails() {
      // GIVEN
      final String username = "testuser";
      final RuntimeException exception = new RuntimeException("Database error");

      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository
              .countLoyaltyPointsByCustomerUsernameAndStatusIn(
                  username, LoyaltyPointStatus.EARNED.name()))
          .thenThrow(exception);

      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.getLoyaltyPoints(
                      username))
          .isEqualTo(exception);

      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository)
          .countLoyaltyPointsByCustomerUsernameAndStatusIn(
              username, LoyaltyPointStatus.EARNED.name());
      verifyNoMoreInteractions(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);
      verifyNoInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst()).contains("Getting earned loyalty points for user:", username);
    }
  }

  @Nested
  @DisplayName("Tests for redeemLoyaltyPoints")
  class RedeemLoyaltyPointsTests {

    @Test
    @DisplayName("redeemLoyaltyPoints throws UnsupportedOperationException")
    void shouldThrowUnsupportedOperationException_whenRedeemingPoints() {
      // GIVEN
      final String username = "testuser";
      final Long orderId = 1L;
      final Long points = 100L;

      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.redeemLoyaltyPoints(
                      username, orderId, points))
          .isInstanceOf(UnsupportedOperationException.class)
          .hasMessage("Redeeming loyalty points is not supported in this implementation.");

      verifyNoInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointMapper);

      assertThat(LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs()).isEmpty();
    }
  }
}
