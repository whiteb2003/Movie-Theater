package com.group5.cpl.model.dto;

import com.group5.cpl.model.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class InvoiceResponseDto {
    private Long id;
    private String movie_title_en;
    private Date movie_date;
    private String schedule;
    private String room;
    private String username;
    private String code;
    private InvoiceStatus status;
    private String price;
    private boolean usePoint;
}
