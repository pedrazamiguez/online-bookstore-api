package es.pedrazamiguez.api.onlinebookstore.domain.service.shipping;

public interface ShippingService {

  void processShipping(String shippingAddress, Long orderId);
}
