package es.pedrazamiguez.assessment.onlinebookstore.domain.service.customer;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;

public interface LoyaltyPointsService {

  Long calculateLoyaltyPoints(Order order);
}
