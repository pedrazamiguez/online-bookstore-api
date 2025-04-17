package es.pedrazamiguez.assessment.onlinebookstore.entity;

import lombok.Data;

@Data
public class Customer {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
}
