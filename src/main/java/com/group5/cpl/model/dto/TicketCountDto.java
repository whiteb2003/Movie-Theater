package com.group5.cpl.model.dto;

import lombok.NoArgsConstructor;

public class TicketCountDto {
    String movie_name;
    long num;

    public TicketCountDto(String movie_name, long num) {
        this.movie_name = movie_name;
        this.num = num;
    }

    public TicketCountDto(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
