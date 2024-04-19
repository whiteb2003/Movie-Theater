package com.group5.cpl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class InvoiceScoreDTO {
    private String code;
    private Double price;
    private String name;
    private Double points;
    private String qr;
    private Boolean userPoint;
    private String status;
}
