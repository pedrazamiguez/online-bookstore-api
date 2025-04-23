package es.pedrazamiguez.assessment.onlinebookstore.application.coordinator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PurchaseStatus;
import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.PurchaseException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;
import es.pedrazamiguez.assessment.onlinebookstore.domain.processor.PurchaseProcessor;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseChainCoordinatorTest {

  @InjectMocks private PurchaseChainCoordinator coordinator;

  @Mock private PurchaseProcessor processor1;

  @Mock private PurchaseProcessor processor2;

  @Test
  void givenSuccessfulProcessors_whenExecuteChain_thenReturnsSuccessContext() {
    // GIVEN
    final String userId = "user123";
    final Long orderId = 1L;
    this.coordinator = new PurchaseChainCoordinator(List.of(this.processor1, this.processor2));

    // Stub processors to set SUCCESS status
    doAnswer(
            invocation -> {
              final PurchaseContext ctx = invocation.getArgument(0);
              ctx.setStatus(PurchaseStatus.SUCCESS);
              return null;
            })
        .when(this.processor1)
        .process(any(PurchaseContext.class));

    doAnswer(
            invocation -> {
              final PurchaseContext ctx = invocation.getArgument(0);
              ctx.setStatus(PurchaseStatus.SUCCESS);
              return null;
            })
        .when(this.processor2)
        .process(any(PurchaseContext.class));

    // WHEN
    final PurchaseContext result = this.coordinator.executeChain(userId, orderId);

    // THEN
    assertThat(result.getStatus()).isEqualTo(PurchaseStatus.SUCCESS);
    assertThat(result.getUserId()).isEqualTo(userId);
    verify(this.processor1).process(any(PurchaseContext.class));
    verify(this.processor2).process(any(PurchaseContext.class));
  }

  @Test
  void givenFailingProcessor_whenExecuteChain_thenThrowsPurchaseExceptionWithContext() {
    // GIVEN
    final String userId = "user123";
    final Long orderId = 1L;
    this.coordinator = new PurchaseChainCoordinator(List.of(this.processor1));
    doThrow(new RuntimeException("Processor failed"))
        .when(this.processor1)
        .process(any(PurchaseContext.class));

    // WHEN
    assertThatThrownBy(() -> this.coordinator.executeChain(userId, orderId))
        .isInstanceOf(PurchaseException.class)
        .satisfies(
            e -> {
              final PurchaseException pe = (PurchaseException) e;
              assertThat(pe.getContext().getStatus()).isEqualTo(PurchaseStatus.FAILED);
              assertThat(pe.getContext().getErrorMessage()).isEqualTo("Processor failed");
              assertThat(pe.getContext().getUserId()).isEqualTo(userId);
            });

    // THEN
    verify(this.processor1).process(any(PurchaseContext.class));
  }

  @Test
  void givenProcessorSetsFailedStatus_whenExecuteChain_thenReturnsFailedContext() {
    // GIVEN
    final String userId = "user123";
    final Long orderId = 1L;
    this.coordinator = new PurchaseChainCoordinator(List.of(this.processor1, this.processor2));
    final String errorMessage = "Validation failed";

    // Stub processor1 to set FAILED status without throwing
    doAnswer(
            invocation -> {
              final PurchaseContext ctx = invocation.getArgument(0);
              ctx.setStatus(PurchaseStatus.FAILED);
              ctx.setErrorMessage(errorMessage);
              return null;
            })
        .when(this.processor1)
        .process(any(PurchaseContext.class));

    // WHEN
    final PurchaseContext result = this.coordinator.executeChain(userId, orderId);

    // THEN
    assertThat(result.getStatus()).isEqualTo(PurchaseStatus.FAILED);
    assertThat(result.getErrorMessage()).isEqualTo(errorMessage);
    assertThat(result.getUserId()).isEqualTo(userId);
    verify(this.processor1).process(any(PurchaseContext.class));
    verify(this.processor2, never()).process(any(PurchaseContext.class));
  }
}
