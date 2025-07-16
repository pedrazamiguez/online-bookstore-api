package es.pedrazamiguez.onlinebookstore.application.service.order;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pricing.discount")
public class DiscountConfigurationProperties {

  private Long defaultMinimumCopies;
  private Regular regular;
  private OldEdition oldEdition;

  @Getter
  @Setter
  public static class Regular {
    private BigDecimal bundle;
  }

  @Getter
  @Setter
  public static class OldEdition {
    private BigDecimal defaultDiscount;
    private BigDecimal bundle;
  }
}
