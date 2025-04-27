package es.pedrazamiguez.assessment.onlinebookstore.repository.mapper;

import es.pedrazamiguez.assessment.onlinebookstore.domain.enums.LoyaltyPointStatus;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.CustomerEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.LoyaltyPointEntity;
import es.pedrazamiguez.assessment.onlinebookstore.repository.entity.OrderEntity;
import java.util.List;
import java.util.stream.LongStream;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoyaltyPointMapper {

    default List<LoyaltyPointEntity> toEarnedLoyaltyPoints(
            final CustomerEntity customerEntity, final OrderEntity orderEntity, final Long points) {

        return LongStream.range(0, points)
                .mapToObj(
                        i -> {
                            final LoyaltyPointEntity loyaltyPointEntity = new LoyaltyPointEntity();
                            loyaltyPointEntity.setCustomer(customerEntity);
                            loyaltyPointEntity.setOrder(orderEntity);
                            loyaltyPointEntity.setStatus(LoyaltyPointStatus.EARNED);
                            return loyaltyPointEntity;
                        })
                .toList();
    }
}
