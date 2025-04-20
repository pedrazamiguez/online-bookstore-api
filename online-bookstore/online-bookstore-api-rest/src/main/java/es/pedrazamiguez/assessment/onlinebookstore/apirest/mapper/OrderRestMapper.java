package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderRestMapper {

  OrderDto toDto(Order order);

  @Mapping(target = "bookId", source = "allocation.book.id")
  @Mapping(target = "isbn", source = "allocation.book.isbn")
  @Mapping(target = "title", source = "allocation.book.title")
  @Mapping(target = "price", source = "allocation.book.price")
  @Mapping(target = "type", source = "allocation.book.type.code")
  @Mapping(target = "quantity", source = "allocation.copies")
  OrderItemDto orderItemToOrderItemDto(OrderItem orderItem);
}
