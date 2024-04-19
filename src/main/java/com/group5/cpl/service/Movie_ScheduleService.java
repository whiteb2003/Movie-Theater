package com.group5.cpl.service;

import com.group5.cpl.model.Movie_Schedule;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

public interface Movie_ScheduleService {

    List<Movie_Schedule> getMovieSchedules();

    Movie_Schedule getMovieSchedulesByID(long id) ;

    List<Movie_Schedule> getMovieSchedulebyMRDid(long mrdID);

    public Page<Movie_Schedule> searchMovieSchedule(int pageNum, int schedule, String roomName, Date movieDate, String movieName);

    public Page<Movie_Schedule> findScheduleWithAdminTicket(int pageNum, int schedule, String roomName, Date movieDate, String movieName, long user_id);
}
