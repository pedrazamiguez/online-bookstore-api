package es.pedrazamiguez.api.onlinebookstore.domain.model;

import lombok.Data;

@Data
public class Customer {
  private Long id;
  private String username;
  private String email;
  private String address;
  private String phone;
}
