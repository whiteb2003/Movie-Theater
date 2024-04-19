package com.group5.cpl.repository;

import com.group5.cpl.model.Movie_Schedule;
import com.group5.cpl.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface Movie_ScheduleRepositoryPaging extends PagingAndSortingRepository<Movie_Schedule,Long> {

    @Query("SELECT ms FROM Movie_Schedule ms WHERE " +
            "(:schedule IS NULL OR :schedule = 0 OR ms.schedule.value = :schedule) " +
            "AND (:roomName IS NULL OR ms.mrd_item.room.room_name LIKE %:roomName%) " +
            "AND (:movieDate IS NULL OR ms.mrd_item.movie_date = :movieDate) " +
            "AND (:movieName IS NULL OR ms.mrd_item.movie.movie_title_en LIKE %:movieName%) " +
            "AND (SELECT COUNT(t) FROM ms.ticketList t) > 0 " +
            "AND ms.mrd_item.movie_date >= CURRENT_DATE")
    Page<Movie_Schedule> findScheduleBy(@Param("schedule") int schedule,
                                  @Param("roomName") String roomName,
                                  @Param("movieDate") Date movieDate,
                                  @Param("movieName") String movieName,
                                  Pageable pageable);


    @Query("SELECT ms FROM Movie_Schedule ms WHERE " +
            "(:schedule IS NULL OR :schedule = 0 OR ms.schedule.value = :schedule) " +
            "AND (:roomName IS NULL OR ms.mrd_item.room.room_name LIKE %:roomName%) " +
            "AND (:movieDate IS NULL OR ms.mrd_item.movie_date = :movieDate) " +
            "AND (:movieName IS NULL OR ms.mrd_item.movie.movie_title_en LIKE %:movieName%) " +
            "AND (SELECT COUNT(t) FROM ms.ticketList t where t.invoice.user_id = :user_id) > 0 " +
            "AND ms.mrd_item.movie_date >= CURRENT_DATE")
    Page<Movie_Schedule> findScheduleWithAdminTicket(@Param("schedule") int schedule,
                                        @Param("roomName") String roomName,
                                        @Param("movieDate") Date movieDate,
                                        @Param("movieName") String movieName,
                                        @Param("user_id") long user_id,
                                        Pageable pageable);
}
