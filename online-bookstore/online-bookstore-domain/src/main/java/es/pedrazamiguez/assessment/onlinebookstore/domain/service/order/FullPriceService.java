package es.pedrazamiguez.assessment.onlinebookstore.domain.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;

public interface FullPriceService {

  void calculate(Order order);
}
