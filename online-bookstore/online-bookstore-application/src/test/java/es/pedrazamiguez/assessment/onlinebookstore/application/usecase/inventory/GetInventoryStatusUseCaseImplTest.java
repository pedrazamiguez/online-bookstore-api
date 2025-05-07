package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.repository.BookCopyRepository;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetInventoryStatusUseCaseImplTest {

  @InjectMocks private GetInventoryStatusUseCaseImpl getInventoryStatusUseCase;

  @Mock private BookCopyRepository bookCopyRepository;

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void givenIncludeOutOfStock_whenGetInventoryStatus_thenGetInventoryStatus(
      final boolean includeOutOfStock) {

    // GIVEN
    final List<BookAllocation> expectedInventoryStatus =
        Instancio.ofList(BookAllocation.class).size(4).create();

    when(this.bookCopyRepository.getInventoryDetails(includeOutOfStock))
        .thenReturn(expectedInventoryStatus);

    // WHEN
    final List<BookAllocation> result =
        this.getInventoryStatusUseCase.getInventoryStatus(includeOutOfStock);

    // THEN
    assertThat(result).isEqualTo(expectedInventoryStatus);
    verify(this.bookCopyRepository).getInventoryDetails(includeOutOfStock);
  }
}
