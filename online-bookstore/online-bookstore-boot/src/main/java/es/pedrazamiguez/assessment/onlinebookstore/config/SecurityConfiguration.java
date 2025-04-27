package es.pedrazamiguez.assessment.onlinebookstore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

  private static final String SUPER_SECRET_PASSWORD = "12345678";

  private final ObjectMapper objectMapper;

  public SecurityConfiguration(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    // / (index)
                    .requestMatchers(HttpMethod.GET, "/")
                    .permitAll()

                    // swagger-ui
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/openapi.yml")
                    .permitAll()

                    // /h2-console
                    .requestMatchers("/h2-console/**")
                    .hasAnyRole("USER", "ADMIN")

                    // All other requests
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception ->
                exception.authenticationEntryPoint(
                    (request, response, authException) -> {
                      response.setStatus(HttpStatus.UNAUTHORIZED.value());
                      response.setContentType(MediaType.APPLICATION_JSON.toString());

                      final Map<String, Object> body = new HashMap<>();
                      body.put("status", HttpStatus.UNAUTHORIZED.name());
                      body.put("message", "Authentication required");
                      body.put("path", request.getRequestURI());
                      body.put("timestamp", LocalDateTime.now());

                      this.objectMapper.writeValue(response.getOutputStream(), body);
                    }))
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(Customizer.withDefaults())
        .authenticationManager(this.authenticationManager());

    return http.build();
  }

  private AuthenticationManager authenticationManager() {
    final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(this.userDetailsService());
    authenticationProvider.setPasswordEncoder(this.passwordEncoder());

    final ProviderManager providerManager = new ProviderManager(authenticationProvider);
    providerManager.setEraseCredentialsAfterAuthentication(false);

    return providerManager;
  }

  private UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(
        this.buildWithUserRole("bob"),
        this.buildWithUserRole("alice"),
        this.buildWithAdminRole("admin"),
        this.buildWithAdminRole("superadmin"));
  }

  private PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  private UserDetails buildWithUserRole(final String username) {
    return User.withUsername(username)
        .password(this.passwordEncoder().encode(SUPER_SECRET_PASSWORD))
        .roles("USER")
        .build();
  }

  private UserDetails buildWithAdminRole(final String username) {
    return User.withUsername(username)
        .password(this.passwordEncoder().encode(SUPER_SECRET_PASSWORD))
        .roles("USER", "ADMIN")
        .build();
  }
}
