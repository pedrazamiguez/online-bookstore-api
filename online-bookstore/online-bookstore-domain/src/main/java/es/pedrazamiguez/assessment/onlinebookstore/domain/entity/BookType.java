package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

import lombok.Data;

@Data
public class BookType {
  private String code;
  private String name;
  private String description;
}
