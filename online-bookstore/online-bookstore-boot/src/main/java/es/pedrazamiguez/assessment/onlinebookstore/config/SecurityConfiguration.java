package es.pedrazamiguez.assessment.onlinebookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
public class SecurityConfiguration {

  private static final String SUPER_SECRET_PASSWORD = "12345678";

  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    // /h2-console
                    .requestMatchers("/h2-console/**")
                    .hasAnyRole("USER", "ADMIN")

                    // swagger-ui
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**")
                    .permitAll()

                    // /v1/books -> GET for USER and ADMIN
                    .requestMatchers(HttpMethod.GET, "/v1/books/**")
                    .hasAnyRole("USER", "ADMIN")

                    // /v1/books -> POST for ADMIN only
                    .requestMatchers(HttpMethod.POST, "/v1/books")
                    .hasRole("ADMIN")

                    // /v1/inventory -> GET for USER and ADMIN
                    .requestMatchers(HttpMethod.GET, "/v1/inventory**")
                    .hasAnyRole("USER", "ADMIN")

                    // /v1/inventory -> POST for ADMIN only
                    .requestMatchers(HttpMethod.POST, "/v1/inventory")
                    .hasRole("ADMIN")

                    // /v1/orders -> GET for USER
                    .requestMatchers(HttpMethod.GET, "/v1/orders/**")
                    .hasRole("USER")

                    // All other requests
                    .anyRequest()
                    .authenticated())
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
