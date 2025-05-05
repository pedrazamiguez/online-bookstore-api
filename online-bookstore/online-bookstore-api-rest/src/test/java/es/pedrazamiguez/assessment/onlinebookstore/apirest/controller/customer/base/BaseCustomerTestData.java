package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.customer.base;

import static org.instancio.Select.all;
import static org.instancio.Select.field;

import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.LoyaltyPointsDto;
import org.instancio.Instancio;

public abstract class BaseCustomerTestData {

  protected LoyaltyPointsDto givenLoyaltyPointsDto() {
    return Instancio.of(LoyaltyPointsDto.class)
        .generate(field(LoyaltyPointsDto::getAvailable), gen -> gen.longs().range(0L, 1000L))
        .create();
  }

  protected Long givenLoyaltyPoints() {
    return Instancio.of(Long.class)
        .supply(all(Long.class), gen -> gen.longRange(1L, 1000L))
        .create();
  }
}
