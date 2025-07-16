package es.pedrazamiguez.api.onlinebookstore.domain.processor;

import es.pedrazamiguez.api.onlinebookstore.domain.model.PurchaseContext;

public interface PurchaseProcessor {

  void process(PurchaseContext context);
}
