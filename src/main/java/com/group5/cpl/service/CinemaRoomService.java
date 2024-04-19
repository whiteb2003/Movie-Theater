package com.group5.cpl.service;

import com.group5.cpl.model.Cinema_Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CinemaRoomService {
    public Cinema_Room addCinemaRoom(Cinema_Room cinemaRoom);

    public Cinema_Room getCinemaRoomByID(long id);

    public List<Cinema_Room> getCinemaRooms();

    public Page<Cinema_Room> searchByName(String name, int page);

}
