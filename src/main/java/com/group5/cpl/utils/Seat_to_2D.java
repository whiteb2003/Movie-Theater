package com.group5.cpl.utils;

import com.group5.cpl.model.Seat;
import com.group5.cpl.model.dto.TicketSeatDto;

import java.util.ArrayList;
import java.util.List;

public class Seat_to_2D {
    public List<List<Seat>> convertTo2D(List<Seat> seats, int numRows, int numCols) {
        List<List<Seat>> seats2D = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            List<Seat> row = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                if (index < seats.size()) {
                    row.add(seats.get(index));
                    index++;
                } else {
                    // Add a dummy seat if the list is shorter than expected
                    row.add(new Seat());
                }
            }
            seats2D.add(row);
        }
        return seats2D;
    }

    public List<List<TicketSeatDto>> convertTo2D_Ticket_Seat_Dto(List<TicketSeatDto> seats, int numRows, int numCols) {
        List<List<TicketSeatDto>> seats2D = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < numRows; i++) {
            List<TicketSeatDto> row = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                if (index < seats.size()) {
                    row.add(seats.get(index));
                    index++;
                } else {
                    // Add a dummy seat if the list is shorter than expected
                    row.add(new TicketSeatDto());
                }
            }
            seats2D.add(row);
        }
        return seats2D;
    }

    public List<Seat> convertTo1D(List<List<Seat>> seats) {
        List<Seat> seats1D = new ArrayList<>();
        for (int i = 0; i < seats.size(); i++) {
            for (int j = 0; j < seats.get(i).size(); j++) {
                seats1D.add(seats.get(i).get(j));
            }
        }
        return seats1D;
    }

}
