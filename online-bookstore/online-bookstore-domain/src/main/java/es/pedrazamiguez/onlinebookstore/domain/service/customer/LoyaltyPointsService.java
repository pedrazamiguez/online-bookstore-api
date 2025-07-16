package es.pedrazamiguez.onlinebookstore.domain.service.customer;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;

public interface LoyaltyPointsService {

  Long calculateLoyaltyPoints(Order order);
}
