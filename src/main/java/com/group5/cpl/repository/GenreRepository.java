package com.group5.cpl.repository;

import com.group5.cpl.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GenreRepository extends JpaRepository<Genre,Long> {
    @Query("SELECT g from Genre g WHERE g.genreName = ?1")
    Genre findByName(String name);
}
