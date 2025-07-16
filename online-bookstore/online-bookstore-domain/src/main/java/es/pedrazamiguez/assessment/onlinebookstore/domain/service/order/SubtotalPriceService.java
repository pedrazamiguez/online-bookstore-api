package es.pedrazamiguez.api.onlinebookstore.domain.service.order;

import es.pedrazamiguez.api.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.api.onlinebookstore.domain.model.PayableAmount;

public interface SubtotalPriceService {

  String getBookTypeCode();

  PayableAmount calculateSubtotal(OrderItem orderItem);
}
