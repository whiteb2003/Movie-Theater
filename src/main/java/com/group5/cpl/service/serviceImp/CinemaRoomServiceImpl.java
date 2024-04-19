package com.group5.cpl.service.serviceImp;

import com.group5.cpl.model.Cinema_Room;
import com.group5.cpl.repository.CinemaRoomRepository;
import com.group5.cpl.repository.CinemaRoomRepositoryPaging;
import com.group5.cpl.service.CinemaRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CinemaRoomServiceImpl implements CinemaRoomService {
    @Autowired

    private CinemaRoomRepository cinemaRoomRepository;
    @Autowired

    private CinemaRoomRepositoryPaging cinemaRoomRepositoryPaging;

    @Override
    public Cinema_Room addCinemaRoom(Cinema_Room cinemaRoom) {
        return cinemaRoomRepository.save(cinemaRoom);
    }

    @Override
    public Cinema_Room getCinemaRoomByID(long id) {
        return cinemaRoomRepository.findById(id).get();
    }

    @Override
    public List<Cinema_Room> getCinemaRooms() {
        return (List<Cinema_Room>) cinemaRoomRepository.findAll();
    }

    @Override
    public Page<Cinema_Room> searchByName(String name, int page) {
        Pageable pageable = PageRequest.of(page - 1, 4);
        return cinemaRoomRepositoryPaging.searchRoomByName(name,pageable);
    }

}
