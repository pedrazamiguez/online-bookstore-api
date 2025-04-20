package es.pedrazamiguez.assessment.onlinebookstore.config;

import java.util.Optional;

import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfiguration {

  private final SecurityService securityService;

  public JpaConfiguration(final SecurityService securityService) {
    this.securityService = securityService;
  }

  @Bean
  public AuditorAware<String> auditorProvider() {
    return () -> Optional.of(this.securityService.getCurrentUserName());
  }
}
