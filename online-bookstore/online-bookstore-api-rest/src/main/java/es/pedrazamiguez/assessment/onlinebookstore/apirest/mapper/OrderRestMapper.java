package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.BookAllocation;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderRestMapper {

  OrderDto toDto(Order order);

  @Mapping(target = "bookId", source = "book.id")
  @Mapping(target = "isbn", source = "book.isbn")
  @Mapping(target = "title", source = "book.title")
  @Mapping(target = "price", source = "book.price")
  @Mapping(target = "type", source = "book.type.code")
  @Mapping(target = "quantity", source = "copies")
  OrderItemDto bookAllocationToOrderItemDto(BookAllocation bookAllocation);
}
