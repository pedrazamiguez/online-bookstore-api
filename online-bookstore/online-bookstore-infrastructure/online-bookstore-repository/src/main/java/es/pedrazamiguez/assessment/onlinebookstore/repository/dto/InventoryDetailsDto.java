package es.pedrazamiguez.assessment.onlinebookstore.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InventoryDetailsDto {

    private Long copies;
    private Timestamp lastUpdatedAt;
    private String lastUpdatedBy;
    private Long bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Integer yearPublished;
    private BigDecimal price;
    private String genre;
    private String typeCode;
}
