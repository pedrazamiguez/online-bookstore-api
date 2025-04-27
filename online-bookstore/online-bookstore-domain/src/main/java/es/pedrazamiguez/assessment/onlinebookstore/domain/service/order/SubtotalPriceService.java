package es.pedrazamiguez.assessment.onlinebookstore.domain.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PayableAmount;

public interface SubtotalPriceService {

  String getBookTypeCode();

  PayableAmount calculateSubtotal(OrderItem orderItem);
}
