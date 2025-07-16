package es.pedrazamiguez.onlinebookstore.domain.service.order;

import es.pedrazamiguez.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.onlinebookstore.domain.model.PayableAmount;

public interface SubtotalPriceService {

  String getBookTypeCode();

  PayableAmount calculateSubtotal(OrderItem orderItem);
}
