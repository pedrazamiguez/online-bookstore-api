package es.pedrazamiguez.api.onlinebookstore.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.api.onlinebookstore.domain.enums.LoyaltyPointStatus;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.CustomerNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.domain.exception.OrderNotFoundException;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.LoyaltyPointEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.OrderEntity;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.CustomerJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.LoyaltyPointJpaRepository;
import es.pedrazamiguez.api.onlinebookstore.repository.jpa.OrderJpaRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import nl.altindag.log.LogCaptor;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoyaltyPointRepositoryImplTest {

  @InjectMocks private LoyaltyPointRepositoryImpl loyaltyPointRepository;

  @Mock private CustomerJpaRepository customerJpaRepository;

  @Mock private OrderJpaRepository orderJpaRepository;

  @Mock private LoyaltyPointJpaRepository loyaltyPointJpaRepository;

  @Captor private ArgumentCaptor<List<LoyaltyPointEntity>> loyaltyPointEntityCaptor;

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
      final String username = Instancio.create(String.class);
      final Long orderId = 1L;
      final Long points = 5L;
      final CustomerEntity customer = Instancio.create(CustomerEntity.class);
      final OrderEntity order = Instancio.create(OrderEntity.class);

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.of(customer));
      when(LoyaltyPointRepositoryImplTest.this.orderJpaRepository.findById(orderId))
          .thenReturn(Optional.of(order));
      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository.saveAll(anyList()))
          .thenAnswer(inv -> inv.getArgument(0));

      // WHEN
      LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
          username, orderId, points);

      // THEN
      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verify(LoyaltyPointRepositoryImplTest.this.orderJpaRepository).findById(orderId);

      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository)
          .saveAll(LoyaltyPointRepositoryImplTest.this.loyaltyPointEntityCaptor.capture());

      final List<LoyaltyPointEntity> captured =
          LoyaltyPointRepositoryImplTest.this.loyaltyPointEntityCaptor.getValue();
      assertThat(captured).hasSize(points.intValue());

      for (final LoyaltyPointEntity entity : captured) {
        assertThat(entity.getCustomer()).isEqualTo(customer);
        assertThat(entity.getOrder()).isEqualTo(order);
        assertThat(entity.getStatus()).isEqualTo(LoyaltyPointStatus.EARNED);
      }

      verifyNoMoreInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints throws CustomerNotFoundException when customer does not exist")
    void shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
      // GIVEN
      final String username = Instancio.create(String.class);
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
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints throws OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundException_whenOrderNotFound() {
      // GIVEN
      final String username = Instancio.create(String.class);
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
      verifyNoInteractions(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints handles zero points correctly")
    void shouldHandleZeroPoints_whenAddingLoyaltyPoints() {
      // GIVEN
      final String username = Instancio.create(String.class);
      final Long orderId = 1L;
      final Long points = 0L;
      final CustomerEntity customer = Instancio.create(CustomerEntity.class);
      final OrderEntity order = Instancio.create(OrderEntity.class);
      final List<LoyaltyPointEntity> loyaltyPoints = Collections.emptyList();

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.of(customer));
      when(LoyaltyPointRepositoryImplTest.this.orderJpaRepository.findById(orderId))
          .thenReturn(Optional.of(order));

      // WHEN
      LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
          username, orderId, points);

      // THEN
      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verify(LoyaltyPointRepositoryImplTest.this.orderJpaRepository).findById(orderId);
      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository).saveAll(loyaltyPoints);
      verifyNoMoreInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst())
          .contains("Adding loyalty points for user:", username, points.toString());
    }

    @Test
    @DisplayName("addLoyaltyPoints handles empty loyalty points list")
    void shouldHandleEmptyLoyaltyPointsList_whenReturnsEmpty() {
      // GIVEN
      final String username = Instancio.create(String.class);
      final Long orderId = 1L;
      final Long points = 0L;
      final CustomerEntity customer = Instancio.create(CustomerEntity.class);
      final OrderEntity order = Instancio.create(OrderEntity.class);
      final List<LoyaltyPointEntity> loyaltyPoints = Collections.emptyList();

      when(LoyaltyPointRepositoryImplTest.this.customerJpaRepository.findByUsername(username))
          .thenReturn(Optional.of(customer));
      when(LoyaltyPointRepositoryImplTest.this.orderJpaRepository.findById(orderId))
          .thenReturn(Optional.of(order));
      when(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository.saveAll(anyList()))
          .thenAnswer(inv -> inv.getArgument(0));

      // WHEN
      LoyaltyPointRepositoryImplTest.this.loyaltyPointRepository.addLoyaltyPoints(
          username, orderId, points);

      // THEN
      verify(LoyaltyPointRepositoryImplTest.this.customerJpaRepository).findByUsername(username);
      verify(LoyaltyPointRepositoryImplTest.this.orderJpaRepository).findById(orderId);

      verify(LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository)
          .saveAll(LoyaltyPointRepositoryImplTest.this.loyaltyPointEntityCaptor.capture());

      final List<LoyaltyPointEntity> captured =
          LoyaltyPointRepositoryImplTest.this.loyaltyPointEntityCaptor.getValue();
      assertThat(captured).hasSize(points.intValue()).isEqualTo(loyaltyPoints);

      verifyNoMoreInteractions(
          LoyaltyPointRepositoryImplTest.this.customerJpaRepository,
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository,
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);

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
      final String username = Instancio.create(String.class);
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
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst()).contains("Getting earned loyalty points for user:", username);
    }

    @Test
    @DisplayName("getLoyaltyPoints returns zero when user has no points")
    void shouldReturnZero_whenUserHasNoPoints() {
      // GIVEN
      final String username = Instancio.create(String.class);

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
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository);

      final List<String> infoLogs = LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs();
      assertThat(infoLogs).hasSize(1);
      assertThat(infoLogs.getFirst()).contains("Getting earned loyalty points for user:", username);
    }

    @Test
    @DisplayName("getLoyaltyPoints propagates repository exception")
    void shouldPropagateException_whenRepositoryFails() {
      // GIVEN
      final String username = Instancio.create(String.class);
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
          LoyaltyPointRepositoryImplTest.this.orderJpaRepository);

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
      final String username = Instancio.create(String.class);
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
          LoyaltyPointRepositoryImplTest.this.loyaltyPointJpaRepository);

      assertThat(LoyaltyPointRepositoryImplTest.this.logCaptor.getInfoLogs()).isEmpty();
    }
  }
}
