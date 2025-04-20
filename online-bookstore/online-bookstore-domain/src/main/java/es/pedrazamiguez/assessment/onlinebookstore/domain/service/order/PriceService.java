package es.pedrazamiguez.assessment.onlinebookstore.domain.service.order;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;

public interface PriceService {

    void calculateSubtotal(BookAllocation bookAllocation);
}
