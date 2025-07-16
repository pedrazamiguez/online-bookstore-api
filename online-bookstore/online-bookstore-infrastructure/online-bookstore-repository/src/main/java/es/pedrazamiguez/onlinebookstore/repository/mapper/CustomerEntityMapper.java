package es.pedrazamiguez.onlinebookstore.repository.mapper;

import es.pedrazamiguez.onlinebookstore.domain.model.Customer;
import es.pedrazamiguez.onlinebookstore.repository.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerEntityMapper {

  Customer toDomain(CustomerEntity customerEntity);
}
