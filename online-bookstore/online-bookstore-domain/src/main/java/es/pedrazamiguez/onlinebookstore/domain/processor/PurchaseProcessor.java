package es.pedrazamiguez.onlinebookstore.domain.processor;

import es.pedrazamiguez.onlinebookstore.domain.model.PurchaseContext;

public interface PurchaseProcessor {

  void process(PurchaseContext context);
}
