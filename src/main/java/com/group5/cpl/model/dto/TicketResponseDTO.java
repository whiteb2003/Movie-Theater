package com.group5.cpl.model.dto;

import lombok.Data;

import java.sql.Date;

@Data

public class TicketResponseDTO {
    private String movie_title_en;
    private Date movie_date;
    private String value;
    private String position;
    private String username;
    private Double price;
    private String status;
    private Long id;
}
