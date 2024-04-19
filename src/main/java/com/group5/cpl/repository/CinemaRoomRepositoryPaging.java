package com.group5.cpl.repository;

import com.group5.cpl.model.Cinema_Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface CinemaRoomRepositoryPaging extends PagingAndSortingRepository<Cinema_Room, Long> {
    @Query("select r from Cinema_Room r where " +
            "(:name is null or :name = '' or r.room_name like %:name%)")
    Page<Cinema_Room> searchRoomByName(@Param("name") String name, Pageable pageable);
}
