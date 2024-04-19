package com.group5.cpl.model.dto;

import com.group5.cpl.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class SeatsDto {
    private List<SeatDto> seats = new ArrayList<>() ;

    public void addSeat(SeatDto seat){
        this.seats.add(seat);
    }

    public List<SeatDto> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDto> seats) {
        this.seats = seats;
    }
}
