package es.pedrazamiguez.onlinebookstore.domain.service.order;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;

public interface CurrentOrderService {

  Order getOrCreateOrder(String username);
}
