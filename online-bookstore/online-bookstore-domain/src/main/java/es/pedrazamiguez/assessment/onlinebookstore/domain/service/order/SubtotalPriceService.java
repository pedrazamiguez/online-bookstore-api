package es.pedrazamiguez.assessment.onlinebookstore.domain.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;

public interface SubtotalPriceService {

  String getBookTypeCode();

  PayableAmount calculateSubtotal(OrderItem orderItem);
}
