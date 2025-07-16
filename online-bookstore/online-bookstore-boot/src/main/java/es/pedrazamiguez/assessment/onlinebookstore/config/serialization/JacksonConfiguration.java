package es.pedrazamiguez.api.onlinebookstore.config.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

  @Bean
  public SimpleModule bigDecimalModule() {
    final SimpleModule module = new SimpleModule();
    module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
    return module;
  }
}
