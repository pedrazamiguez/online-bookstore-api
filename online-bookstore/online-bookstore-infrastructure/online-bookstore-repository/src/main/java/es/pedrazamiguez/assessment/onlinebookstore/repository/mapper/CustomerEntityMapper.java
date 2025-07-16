package es.pedrazamiguez.api.onlinebookstore.repository.mapper;

import es.pedrazamiguez.api.onlinebookstore.domain.model.Customer;
import es.pedrazamiguez.api.onlinebookstore.repository.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerEntityMapper {

  Customer toDomain(CustomerEntity customerEntity);
}
