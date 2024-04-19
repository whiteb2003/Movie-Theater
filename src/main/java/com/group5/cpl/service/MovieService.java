package com.group5.cpl.service;

import com.group5.cpl.model.Movie;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieService
{
    Page<Movie> getActiveMovies(int pageNumber,String keyword);

    Page<Movie> getHiddenMovies(int pageNumber,String keyword);

    void addMovie(Movie movie);

    Page<Movie> listAll(int pageNumber,String keyword);

    Page<Movie> listAllUpComming(int pageNumber,String keyword);

    Movie findById(Long Id);

    Movie findByENGName(String name);

    Movie findByVIEName(String name);

    List<Movie> listCurrent();
}
