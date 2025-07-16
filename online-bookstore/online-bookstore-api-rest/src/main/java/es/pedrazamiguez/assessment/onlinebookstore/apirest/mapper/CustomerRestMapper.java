package es.pedrazamiguez.api.onlinebookstore.apirest.mapper;

import es.pedrazamiguez.api.onlinebookstore.openapi.model.LoyaltyPointsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerRestMapper {

  @Mapping(target = "available", source = "points")
  LoyaltyPointsDto toLoyaltyPointsDto(Long points);
}
