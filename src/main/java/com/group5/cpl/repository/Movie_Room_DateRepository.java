package com.group5.cpl.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.group5.cpl.model.Movie_Room_Date;

@Repository
public interface Movie_Room_DateRepository extends JpaRepository<Movie_Room_Date, Long> {
    @Query("SELECT mrd FROM Movie_Room_Date mrd WHERE mrd.movie.movie_id = ?1")
    List<Movie_Room_Date> findByMovieMovieId(Long movieId);

    @Query("SELECT mrd FROM Movie_Room_Date mrd WHERE mrd.room.room_name = ?1 and mrd.movie.movie_id != ?2")
    List<Movie_Room_Date> findByRoomNameAndDate(String room_name, String movie_id);

    @Query("SELECT mrd FROM Movie_Room_Date mrd WHERE mrd.movie.movie_id = ?1 and mrd.movie_date> CURRENT_DATE")
    List<Movie_Room_Date> findByMovieId(Long movieId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Movie_Room_Date mrd WHERE mrd.mrd_id = ?1")
    void deleteByMrd_id(Long mrd_id);

    @Query("SELECT mrd FROM Movie_Room_Date mrd JOIN FETCH mrd.schedules WHERE mrd.movie_date = :date")
    List<Movie_Room_Date> findAllMovieByDateWithSchedules(@Param("date") Date date);

    @Query("SELECT mrd FROM Movie_Room_Date mrd JOIN FETCH mrd.schedules WHERE mrd.movie_date = CURRENT_DATE")
    List<Movie_Room_Date> findAllMovieToday();

    @Query("SELECT mrd FROM Movie_Room_Date mrd JOIN FETCH mrd.schedules WHERE mrd.movie_date = :date and mrd.movie.movie_id = :id")
    List<Movie_Room_Date> findAllMovieByDateWithSchedulesAndMovie(@Param("date") Date date, @Param("id") Long id);

    @Query("select distinct mrd.movie_date from Movie_Room_Date mrd where mrd.movie.movie_id = ?1 and mrd.movie_date > CURRENT_DATE")
    List<String> getMoviesById(Long id);

    @Query("select mrd from Movie_Room_Date mrd where mrd.movie.movie_id = ?1 and mrd.room.room_name=?2 and mrd.movie_date=?3")
    List<Movie_Room_Date> getMoviesByAndRoomAndDate(Long id, String name, Date date);

    @Query("select mrd from Movie_Room_Date mrd where mrd.movie_date = ?1 and mrd.movie.movie_id = ?2")
    List<Movie_Room_Date> getByDayAndMovie(Date date, long movie_id);

    @Query("select mrd from Movie_Room_Date mrd where mrd.movie.movie_id = ?1")
    List<Movie_Room_Date> getByMovie(long movie_id);
}
