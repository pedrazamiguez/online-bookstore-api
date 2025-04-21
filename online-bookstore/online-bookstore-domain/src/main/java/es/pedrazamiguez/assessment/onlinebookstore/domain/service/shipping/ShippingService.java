package es.pedrazamiguez.assessment.onlinebookstore.domain.service.shipping;

public interface ShippingService {

  void processShipping(String shippingAddress, Long orderId);
}
