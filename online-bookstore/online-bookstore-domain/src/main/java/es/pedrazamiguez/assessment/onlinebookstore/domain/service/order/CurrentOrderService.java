package es.pedrazamiguez.api.onlinebookstore.domain.service.order;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;

public interface CurrentOrderService {

  Order getOrCreateOrder(String username);
}
