package es.pedrazamiguez.onlinebookstore.utils;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestSecurityUtils {

  private TestSecurityUtils() {}

  public static void setAuthenticatedUser(final String username, final String... roles) {
    final List<SimpleGrantedAuthority> authorities =
        Stream.of(roles).map(SimpleGrantedAuthority::new).toList();
    final Authentication authentication =
        new TestingAuthenticationToken(username, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  public static String getAuthenticatedUserName() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      return authentication.getName();
    }
    return null;
  }

  public static void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }
}
