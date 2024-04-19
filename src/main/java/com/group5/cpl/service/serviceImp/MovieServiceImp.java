package com.group5.cpl.service.serviceImp;

import com.group5.cpl.model.Movie;
import com.group5.cpl.model.enums.Movie_status;
import com.group5.cpl.repository.MovieRepository;
import com.group5.cpl.repository.MovieRepositoryPaging;
import com.group5.cpl.service.MovieService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImp implements MovieService {
    @Autowired
    MovieRepository repository;
    @Autowired
    MovieRepositoryPaging repositoryPaging;

    @Override
    public Page<Movie> getActiveMovies(int pageNumber, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 6);
        if (keyword != null) {
            return repositoryPaging.findAllActive(keyword, pageable);
        }
        return repositoryPaging.findAllActive(pageable);
    }

    @Override
    public Page<Movie> getHiddenMovies(int pageNumber, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 6);
        if (keyword != null) {
            return repositoryPaging.findAllHidden(keyword, pageable);
        }
        return repositoryPaging.findAllHidden(pageable);
    }

    @Override
    public void addMovie(Movie movie) {
        repository.save(movie);
    }

    @Override
    public Page<Movie> listAll(int pageNumber, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 10);
        if (keyword != null) {
            return repositoryPaging.findAllActiveHaveDateLess(keyword, pageable);
        }
        return repositoryPaging.findAllActiveHaveDateLess(pageable);
    }

    @Override
    public Page<Movie> listAllUpComming(int pageNumber, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 10);
        if (keyword != null) {
            return repositoryPaging.findAllActiveHaveDateLarger(keyword, pageable);
        }
        return repositoryPaging.findAllActiveHaveDateLarger(pageable);
    }

    @Override
    public Movie findById(Long Id) {
        Optional<Movie> movie = repository.findById(Id);
        Movie m = movie.get();
        return m;
    }

    @Override
    public Movie findByENGName(String name) {
        return repository.findAllByNameENG(name);
    }

    @Override
    public Movie findByVIEName(String name) {
        return repository.findAllByNameVIE(name);
    }

    @Override
    public List<Movie> listCurrent() {
        return repository.findAllCurrentMovie();
    }

}
