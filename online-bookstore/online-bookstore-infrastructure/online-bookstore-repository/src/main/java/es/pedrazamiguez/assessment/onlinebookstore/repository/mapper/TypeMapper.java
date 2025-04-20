package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TypeMapper {

  default Timestamp toTimestamp(final LocalDateTime localDateTime) {
    return Timestamp.valueOf(localDateTime);
  }
}
