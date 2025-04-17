package es.pedrazamiguez.assessment.onlinebookstore.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookType {
    private Long id;
    private String description;
    private BigDecimal permanentDiscountFactor;
    private BigDecimal bundleDiscountFactor;
    private Integer bundleDiscountThreshold;
}
