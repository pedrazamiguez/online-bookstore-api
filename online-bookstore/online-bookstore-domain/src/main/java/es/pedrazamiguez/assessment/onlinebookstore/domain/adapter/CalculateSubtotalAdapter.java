package es.pedrazamiguez.assessment.onlinebookstore.domain.adapter;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PayableAmount;

public interface CalculateSubtotalAdapter {

    PayableAmount calculateSubtotal(OrderItem orderItem);
}
