package com.group5.cpl.service.serviceImp;

import com.group5.cpl.model.Movie_Schedule;
import com.group5.cpl.repository.Movie_ScheduleRepository;
import com.group5.cpl.repository.Movie_ScheduleRepositoryPaging;
import com.group5.cpl.service.MovieService;
import com.group5.cpl.service.Movie_ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class Movie_ScheduleServiceImpl implements Movie_ScheduleService {
    @Autowired
    Movie_ScheduleRepository movie_scheduleRepository;
    @Autowired
    Movie_ScheduleRepositoryPaging movie_scheduleRepositoryPaging;

    @Override
    public List<Movie_Schedule> getMovieSchedules() {
        return movie_scheduleRepository.findAll();
    }

    @Override
    public Movie_Schedule getMovieSchedulesByID(long id) {
        return movie_scheduleRepository.findById(id).get();
    }

    @Override
    public List<Movie_Schedule> getMovieSchedulebyMRDid(long mrdID){
        return movie_scheduleRepository.getByMrdId(mrdID);
    }

    @Override
    public Page<Movie_Schedule> searchMovieSchedule(int pageNum, int schedule, String roomName, Date movieDate, String movieName) {
        Pageable pageable = PageRequest.of(pageNum - 1, 4);
        return movie_scheduleRepositoryPaging.findScheduleBy(schedule, roomName, movieDate, movieName, pageable);
    }

    @Override
    public Page<Movie_Schedule> findScheduleWithAdminTicket(int pageNum, int schedule, String roomName, Date movieDate, String movieName, long user_id) {
        Pageable pageable = PageRequest.of(pageNum - 1, 4);
        Page<Movie_Schedule> movie_schedules = movie_scheduleRepositoryPaging.findScheduleWithAdminTicket(schedule,roomName,movieDate,movieName,user_id,pageable);
        return movie_schedules;
    }
}
