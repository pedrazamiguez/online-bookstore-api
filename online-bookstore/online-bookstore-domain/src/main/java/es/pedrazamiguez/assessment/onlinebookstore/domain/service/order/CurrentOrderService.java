package es.pedrazamiguez.assessment.onlinebookstore.domain.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;

public interface CurrentOrderService {

  Order getOrCreateOrder(String username);
}
