package es.pedrazamiguez.assessment.onlinebookstore.domain.processor;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.PurchaseContext;

public interface PurchaseProcessor {

  void process(PurchaseContext context);
}
