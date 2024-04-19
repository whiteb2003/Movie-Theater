package com.group5.cpl.service.serviceImp;

import com.group5.cpl.model.Cinema_Room;
import com.group5.cpl.model.Seat;
import com.group5.cpl.repository.SeatRepository;
import com.group5.cpl.service.CinemaRoomService;
import com.group5.cpl.service.SeatService;
import com.group5.cpl.utils.Movie_Seat_Formatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;
    @Override
    public Seat addSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @Override
    public Seat getSeatById(long Id) {
        return seatRepository.findById(Id).get();
    }

    @Override
    public List<Seat> getSeats() {
        return (List<Seat>) seatRepository.findAll();
    }

    @Override
    public void deleteSeat(long Id) {
        seatRepository.delete(getSeatById(Id));
    }

    @Override
    public List<Seat> getSeatByRoomID(long id) {
        return seatRepository.getSeatByRoomId(id);
    }
}
