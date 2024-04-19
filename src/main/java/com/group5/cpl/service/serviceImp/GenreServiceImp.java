package com.group5.cpl.service.serviceImp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group5.cpl.model.Genre;
import com.group5.cpl.repository.GenreRepository;
import com.group5.cpl.service.GenreService;
@Service
public class GenreServiceImp implements GenreService{
    @Autowired GenreRepository genreRepository;
    @Override
    public Genre getGenreByName(String name) {
        Genre g = genreRepository.findByName(name);
        return g;
    }
    @Override
    public List<Genre> listAll() {
        return genreRepository.findAll();
    }

    
}
