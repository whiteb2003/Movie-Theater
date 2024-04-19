package com.group5.cpl.service;

import com.group5.cpl.model.Seat;

import java.util.List;

public interface SeatService {
    public Seat addSeat(Seat seat);

    public Seat getSeatById(long Id);

    public List<Seat> getSeats();

    public void deleteSeat(long Id);

    public List<Seat> getSeatByRoomID(long id);
}
