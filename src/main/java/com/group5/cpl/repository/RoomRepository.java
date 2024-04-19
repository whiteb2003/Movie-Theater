package com.group5.cpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.group5.cpl.model.Cinema_Room;

@Repository
public interface RoomRepository extends JpaRepository<Cinema_Room, Long>{
    @Query("SELECT u from Cinema_Room u WHERE u.room_name = ?1")
    Cinema_Room findByName(String name);
}
