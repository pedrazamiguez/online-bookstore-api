package es.pedrazamiguez.assessment.onlinebookstore.domain.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;

public interface RecalculatePriceService {

  void recalculate(Order order);
}
