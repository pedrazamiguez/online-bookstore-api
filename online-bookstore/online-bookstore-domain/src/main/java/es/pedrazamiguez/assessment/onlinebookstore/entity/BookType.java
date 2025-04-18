package es.pedrazamiguez.assessment.onlinebookstore.entity;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class BookType {
    private Long id;
    private String description;
    private BigDecimal permanentDiscountFactor;
    private BigDecimal bundleDiscountFactor;
    private Integer bundleDiscountThreshold;
}
