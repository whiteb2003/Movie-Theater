package com.group5.cpl.service.serviceImp;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group5.cpl.model.Movie_Room_Date;
import com.group5.cpl.repository.Movie_Room_DateRepository;
import com.group5.cpl.service.Movie_Room_DateService;
@Service
public class Movie_Room_DateServiceImp implements Movie_Room_DateService{
    @Autowired Movie_Room_DateRepository movie_Room_DateRepository;
    @Override
    public void save(Movie_Room_Date mrd) {
        movie_Room_DateRepository.save(mrd);
    }
    @Override
    public List<Movie_Room_Date> getByMovieId(Long movieId) {
        return movie_Room_DateRepository.findByMovieMovieId(movieId);
    }
    @Override
    public void delete(Long mrd_id) {
        movie_Room_DateRepository.deleteByMrd_id(mrd_id);
    }
    @Override
    public List<Movie_Room_Date> getByRoomAndMovie(String roomName,String movie_id) {
        return movie_Room_DateRepository.findByRoomNameAndDate(roomName,movie_id);
    }
    @Override
    public Movie_Room_Date getByMRDid(Long mrd_id) {
        return movie_Room_DateRepository.findById(mrd_id).get();
    }

    @Override
    public List<Movie_Room_Date> getByDateAndMovie(Date date, long movie_id) {
        return movie_Room_DateRepository.getByDayAndMovie(date, movie_id);
    }


}
