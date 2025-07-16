package es.pedrazamiguez.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.onlinebookstore.openapi.model.OrderItemDto;
import es.pedrazamiguez.onlinebookstore.openapi.model.PurchaseRequestDto;
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
  @Mapping(target = "discountRate", source = "payableAmount.discount")
  @Mapping(
      target = "discountPercentage",
      expression = "java( orderItem.getPayableAmount().getDiscountPercentage() )")
  @Mapping(target = "subtotal", source = "payableAmount.subtotal")
  OrderItemDto orderItemToOrderItemDto(OrderItem orderItem);

  Order toDomain(PurchaseRequestDto purchaseRequestDto);
}
