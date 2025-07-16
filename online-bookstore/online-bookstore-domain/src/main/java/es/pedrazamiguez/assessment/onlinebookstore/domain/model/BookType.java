package es.pedrazamiguez.api.onlinebookstore.domain.model;

import lombok.Data;

@Data
public class BookType {
  private String code;
  private String name;
  private String description;
}
