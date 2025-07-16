package es.pedrazamiguez.onlinebookstore.domain.adapter;

import es.pedrazamiguez.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.onlinebookstore.domain.model.PayableAmount;

public interface CalculateSubtotalAdapter {

  PayableAmount calculateSubtotal(OrderItem orderItem);
}
