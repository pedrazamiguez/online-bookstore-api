package es.pedrazamiguez.onlinebookstore.application.service.security;

import es.pedrazamiguez.onlinebookstore.domain.exception.CurrentUserNotFoundException;
import es.pedrazamiguez.onlinebookstore.domain.service.security.SecurityService;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

  @Override
  public String getCurrentUserName() {
    final Optional<String> optionalUsername =
        Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);

    return optionalUsername.orElseThrow(CurrentUserNotFoundException::new);
  }
}
