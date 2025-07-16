package es.pedrazamiguez.api.onlinebookstore.domain.exception;

import lombok.Getter;

@Getter
public class CustomerNotFoundException extends RuntimeException {

  private final String username;

  public CustomerNotFoundException(final String username) {
    super("Customer with username " + username + " not found");
    this.username = username;
  }
}
