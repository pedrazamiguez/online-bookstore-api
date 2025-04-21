package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import java.util.Optional;

public interface OrderRepository {

  Optional<Order> findCreatedOrderForCustomer(String username);

  Order createNewOrder(String username);

  Order saveOrderItem(Long orderId, Long bookId, Long quantity);

  Order deleteOrderItem(Long orderId, Long bookId, Long quantity);

  Order deleteOrderItems(Long orderId);

  Order purchaseOrder(Long orderId, Order orderRequest);
}
