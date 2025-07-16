package es.pedrazamiguez.api.onlinebookstore.domain.service.customer;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Order;

public interface LoyaltyPointsService {

  Long calculateLoyaltyPoints(Order order);
}
