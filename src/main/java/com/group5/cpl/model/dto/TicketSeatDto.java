package com.group5.cpl.model.dto;

import com.group5.cpl.model.enums.TicketStatus;

public class TicketSeatDto {
    long Ticket_id;
    long Seat_id;
    String Seat_position;
    String ticketStatus;
    String username;
    String usermail;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsermail() {
        return usermail;
    }

    public void setUsermail(String usermail) {
        this.usermail = usermail;
    }

    public long getTicket_id() {
        return Ticket_id;
    }

    public void setTicket_id(long ticket_id) {
        Ticket_id = ticket_id;
    }

    public long getSeat_id() {
        return Seat_id;
    }

    public void setSeat_id(long seat_id) {
        Seat_id = seat_id;
    }

    public String getSeat_position() {
        return Seat_position;
    }

    public void setSeat_position(String seat_position) {
        Seat_position = seat_position;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
