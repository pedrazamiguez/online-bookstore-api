package es.pedrazamiguez.api.onlinebookstore.domain.adapter;

import es.pedrazamiguez.api.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.api.onlinebookstore.domain.model.PayableAmount;

public interface CalculateSubtotalAdapter {

  PayableAmount calculateSubtotal(OrderItem orderItem);
}
