package es.pedrazamiguez.assessment.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Order;
import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.OrderItem;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.OrderItemDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderRestMapper {

  @Mapping(target = "totalPrice", source = "totalPrice", qualifiedByName = "withPrecision")
  OrderDto toDto(Order order);

  @Mapping(target = "bookId", source = "allocation.book.id")
  @Mapping(target = "isbn", source = "allocation.book.isbn")
  @Mapping(target = "title", source = "allocation.book.title")
  @Mapping(target = "price", source = "allocation.book.price", qualifiedByName = "withPrecision")
  @Mapping(target = "type", source = "allocation.book.type.code")
  @Mapping(target = "quantity", source = "allocation.copies")
  @Mapping(
      target = "discountRate",
      source = "payableAmount.discount",
      qualifiedByName = "withPrecision")
  @Mapping(
      target = "discountPercentage",
      source = "payableAmount.discount",
      qualifiedByName = "discountPercentage")
  @Mapping(
      target = "subtotal",
      source = "payableAmount.subtotal",
      qualifiedByName = "withPrecision")
  OrderItemDto orderItemToOrderItemDto(OrderItem orderItem);

  @Named("discountPercentage")
  default BigDecimal getDiscountPercentage(final BigDecimal discountRate) {

    final BigDecimal percentage =
        BigDecimal.ONE.subtract(discountRate).multiply(new BigDecimal("100.00"));

    return this.withPrecision(percentage);
  }

  @Named("withPrecision")
  default BigDecimal withPrecision(final BigDecimal value) {
    if (value == null) {
      return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    return value.setScale(2, RoundingMode.HALF_UP);
  }
}
