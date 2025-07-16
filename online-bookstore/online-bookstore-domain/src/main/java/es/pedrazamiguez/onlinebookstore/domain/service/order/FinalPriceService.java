package es.pedrazamiguez.onlinebookstore.domain.service.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;

public interface FinalPriceService {

  void calculate(Order order);
}
