package com.group5.cpl.service;

import java.sql.Date;
import java.util.List;

import com.group5.cpl.model.Movie_Room_Date;

public interface Movie_Room_DateService {
    public void save(Movie_Room_Date mrd);
    
    public List<Movie_Room_Date> getByMovieId(Long movieId);

    public void delete(Long mrd_id);

    public List<Movie_Room_Date> getByRoomAndMovie(String roomName,String movie_id);

    public Movie_Room_Date getByMRDid(Long mrd_id);

    public List<Movie_Room_Date> getByDateAndMovie(Date date, long movie_id);

}
