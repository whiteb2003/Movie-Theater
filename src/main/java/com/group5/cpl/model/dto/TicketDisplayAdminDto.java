package com.group5.cpl.model.dto;

import com.group5.cpl.model.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDisplayAdminDto {
    long ticketID;
    String movie_name;
    String customer_name;
    Date movie_date;
    String movie_schedule;
    String seat_position;
    Double seat_price;
    private TicketStatus ticketStatus;

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public long getTicketID() {
        return ticketID;
    }

    public void setTicketID(long ticketID) {
        this.ticketID = ticketID;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public Date getMovie_date() {
        return movie_date;
    }

    public void setMovie_date(Date movie_date) {
        this.movie_date = movie_date;
    }

    public String getMovie_schedule() {
        return movie_schedule;
    }

    public void setMovie_schedule(String movie_schedule) {
        this.movie_schedule = movie_schedule;
    }

    public String getSeat_position() {
        return seat_position;
    }

    public void setSeat_position(String seat_position) {
        this.seat_position = seat_position;
    }

    public Double getSeat_price() {
        return seat_price;
    }

    public void setSeat_price(Double seat_price) {
        this.seat_price = seat_price;
    }
}
