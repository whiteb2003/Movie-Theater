package com.group5.cpl.repository;

import com.group5.cpl.model.Cinema_Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CinemaRoomRepository extends CrudRepository<Cinema_Room, Long> {
}
