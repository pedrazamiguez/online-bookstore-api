package es.pedrazamiguez.assessment.onlinebookstore.application.service.security;

import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.CurrentUserNotFoundException;
import es.pedrazamiguez.assessment.onlinebookstore.domain.service.security.SecurityService;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

  @Override
  public String getCurrentUserName() {
    final Optional<String> optionalUsername =
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    return optionalUsername.orElseThrow(CurrentUserNotFoundException::new);
  }
}
