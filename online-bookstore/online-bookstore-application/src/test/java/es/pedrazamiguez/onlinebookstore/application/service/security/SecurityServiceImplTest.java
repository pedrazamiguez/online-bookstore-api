package es.pedrazamiguez.onlinebookstore.application.service.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import es.pedrazamiguez.onlinebookstore.domain.exception.CurrentUserNotFoundException;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

  @InjectMocks private SecurityServiceImpl securityService;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("When user is authenticated, returns username")
  void givenAuthenticatedUser_whenGetCurrentUserName_thenReturnsUsername() {
    // GIVEN
    final String expectedUsername = Instancio.create(String.class);
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);
    when(this.authentication.isAuthenticated()).thenReturn(true);
    when(this.authentication.getName()).thenReturn(expectedUsername);

    try (final MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(this.securityContext);

      // WHEN
      final String result = this.securityService.getCurrentUserName();

      // THEN
      assertThat(result).isEqualTo(expectedUsername);
    }
  }

  @Test
  @DisplayName("When SecurityContext is null, throws CurrentUserNotFoundException")
  void givenNullSecurityContext_whenGetCurrentUserName_thenThrowsCurrentUserNotFoundException() {
    // GIVEN
    try (final MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(null);

      // WHEN
      assertThatThrownBy(() -> this.securityService.getCurrentUserName())
          .isInstanceOf(CurrentUserNotFoundException.class);
    }
  }

  @Test
  @DisplayName("When Authentication is null, throws CurrentUserNotFoundException")
  void givenNullAuthentication_whenGetCurrentUserName_thenThrowsCurrentUserNotFoundException() {
    // GIVEN
    when(this.securityContext.getAuthentication()).thenReturn(null);

    try (final MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(this.securityContext);

      // WHEN
      assertThatThrownBy(() -> this.securityService.getCurrentUserName())
          .isInstanceOf(CurrentUserNotFoundException.class);
    }
  }

  @Test
  @DisplayName("When user is not authenticated, throws CurrentUserNotFoundException")
  void givenUnauthenticatedUser_whenGetCurrentUserName_thenThrowsCurrentUserNotFoundException() {
    // GIVEN
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);
    when(this.authentication.isAuthenticated()).thenReturn(false);

    try (final MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(this.securityContext);

      // WHEN
      assertThatThrownBy(() -> this.securityService.getCurrentUserName())
          .isInstanceOf(CurrentUserNotFoundException.class);
    }
  }

  @Test
  @DisplayName("When username is null, throws CurrentUserNotFoundException")
  void givenNullUsername_whenGetCurrentUserName_thenThrowsCurrentUserNotFoundException() {
    // GIVEN
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);
    when(this.authentication.isAuthenticated()).thenReturn(true);
    when(this.authentication.getName()).thenReturn(null);

    try (final MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(this.securityContext);

      // WHEN
      assertThatThrownBy(() -> this.securityService.getCurrentUserName())
          .isInstanceOf(CurrentUserNotFoundException.class);
    }
  }
}
