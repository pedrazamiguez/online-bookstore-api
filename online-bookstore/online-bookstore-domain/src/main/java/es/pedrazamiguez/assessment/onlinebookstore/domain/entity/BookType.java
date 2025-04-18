package es.pedrazamiguez.assessment.onlinebookstore.domain.entity;

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
