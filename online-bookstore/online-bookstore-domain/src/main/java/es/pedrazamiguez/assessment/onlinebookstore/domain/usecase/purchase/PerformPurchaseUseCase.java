package es.pedrazamiguez.assessment.onlinebookstore.domain.usecase.purchase;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.PaymentMethod;

@FunctionalInterface
public interface PerformPurchaseUseCase {

  Order purchase(PaymentMethod paymentMethod);
}
