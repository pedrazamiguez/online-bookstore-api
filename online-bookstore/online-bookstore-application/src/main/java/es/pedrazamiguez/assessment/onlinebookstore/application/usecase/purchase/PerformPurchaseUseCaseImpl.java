package es.pedrazamiguez.assessment.onlinebookstore.application.usecase.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;
import es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.purchase.PerformPurchaseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformPurchaseUseCaseImpl implements PerformPurchaseUseCase {

  @Override
  public Order purchase(final PaymentMethod paymentMethod) {
    return null;
  }
}
