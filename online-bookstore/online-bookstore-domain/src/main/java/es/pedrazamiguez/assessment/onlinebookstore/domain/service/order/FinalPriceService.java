package es.pedrazamiguez.api.onlinebookstore.domain.service.order;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;

public interface FinalPriceService {

  void calculate(Order order);
}
