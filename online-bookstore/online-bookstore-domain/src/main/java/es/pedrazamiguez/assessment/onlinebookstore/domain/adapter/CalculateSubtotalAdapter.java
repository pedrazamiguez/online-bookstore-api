package es.pedrazamiguez.assessment.onlinebookstore.domain.adapter;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.PayableAmount;

public interface CalculateSubtotalAdapter {

  PayableAmount calculateSubtotal(OrderItem orderItem);
}
