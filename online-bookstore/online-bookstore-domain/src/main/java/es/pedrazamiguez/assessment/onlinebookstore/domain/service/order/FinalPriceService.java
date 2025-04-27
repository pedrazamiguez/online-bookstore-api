package es.pedrazamiguez.assessment.onlinebookstore.domain.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;

public interface FinalPriceService {

    void calculate(Order order);
}
