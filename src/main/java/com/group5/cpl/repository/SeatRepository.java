package com.group5.cpl.repository;

import com.group5.cpl.model.Seat;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SeatRepository extends CrudRepository<Seat, Long> {
    @Query("SELECT s FROM Seat s WHERE s.id = ?1")
    Seat getSeatById(Long id);

    @Query("SELECT s from Seat s WHERE s.room.room_id = ?1")
    List<Seat> getSeatByRoomId(Long id);
}

