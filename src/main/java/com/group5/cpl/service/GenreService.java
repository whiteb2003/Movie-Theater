package com.group5.cpl.service;

import java.util.List;

import com.group5.cpl.model.Genre;

public interface GenreService {
    public Genre getGenreByName(String name);
    public List<Genre> listAll();
}
