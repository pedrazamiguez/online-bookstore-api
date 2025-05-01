package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.model.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderItemDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.PurchaseRequestDto;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

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
      source = "payableAmount.discount",
      qualifiedByName = "discountPercentage")
  @Mapping(target = "subtotal", source = "payableAmount.subtotal")
  OrderItemDto orderItemToOrderItemDto(OrderItem orderItem);

  Order toDomain(PurchaseRequestDto purchaseRequestDto);

  @Named("discountPercentage")
  default BigDecimal getDiscountPercentage(final BigDecimal discountRate) {
    return BigDecimal.ONE.subtract(discountRate).multiply(new BigDecimal("100.00"));
  }
}
