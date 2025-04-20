package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.entity.Customer;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerEntityMapper {

  Customer toDomain(CustomerEntity customerEntity);
}
