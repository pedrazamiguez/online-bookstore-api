package es.pedrazamiguez.api.onlinebookstore.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class InMemorySecurityConfig {

  @Bean
  public AuthenticationManager authenticationManager(
      final UserDetailsService userDetailsService, final PasswordEncoder passwordEncoder) {

    final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    final ProviderManager manager = new ProviderManager(provider);
    manager.setEraseCredentialsAfterAuthentication(false);
    return manager;
  }

  @Bean
  public UserDetailsService userDetailsService(final PasswordEncoder passwordEncoder) {
    return new InMemoryUserDetailsManager(
        User.withUsername("bob").password(passwordEncoder.encode("12345678")).roles("USER").build(),
        User.withUsername("alice")
            .password(passwordEncoder.encode("12345678"))
            .roles("USER")
            .build(),
        User.withUsername("admin")
            .password(passwordEncoder.encode("12345678"))
            .roles("USER", "ADMIN")
            .build(),
        User.withUsername("superadmin")
            .password(passwordEncoder.encode("12345678"))
            .roles("USER", "ADMIN")
            .build());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
