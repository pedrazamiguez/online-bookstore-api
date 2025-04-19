package es.pedrazamiguez.assessment.onlinebookstore.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfiguration {

  @Bean
  public AuditorAware<String> auditorProvider() {
    // FIXME: Implement a proper auditor provider
    return () -> Optional.of("api_user");
  }
}
