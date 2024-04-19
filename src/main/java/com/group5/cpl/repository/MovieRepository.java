package com.group5.cpl.repository;

import com.group5.cpl.model.Movie;
import com.group5.cpl.model.enums.Movie_status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

        @Query("select u from Movie u where u.movie_title_en = ?1")
        Movie findAllByNameENG(String name);

        @Query("select u from Movie u where u.movie_title_vn = ?1")
        Movie findAllByNameVIE(String name);

        @Query("select u from Movie u where u.movie_title_en = ?1 and u.movie_id != ?2")
        Movie findAllByNameENGAndId(String name, String movie_id);

        @Query("select u from Movie u where u.movie_title_vn = ?1 and u.movie_id != ?2")
        Movie findAllByNameVIEAndId(String name, String movie_id);

        @Transactional
        @Modifying
        @Query("UPDATE Movie u SET u.movie_status = 1 WHERE u.movie_id = ?1")
        void hiddenMovie(Long movie_id);

        @Transactional
        @Modifying
        @Query("UPDATE Movie u SET u.movie_status = 0 WHERE u.movie_id = ?1")
        void activeMovie(Long movie_id);

        @Query("SELECT m FROM Movie m " +
                        "WHERE m.movie_status = 0 " +
                        "AND m.release_date <= CURRENT_DATE " +
                        "AND EXISTS (" +
                        "    SELECT 1 " +
                        "    FROM m.movie_Room_Dates rd " +
                        "    GROUP BY rd.movie " +
                        "    HAVING MAX(rd.movie_date) >= CURRENT_DATE" +
                        ")")
        List<Movie> getMoviesInTheater();

        @Query("SELECT m FROM Movie m " +
                        "WHERE m.movie_status = 0 " +
                        "AND EXISTS (" +
                        "    SELECT 1 " +
                        "    FROM m.movie_Room_Dates rd " +
                        "    GROUP BY rd.movie " +
                        "    HAVING MAX(rd.movie_date) >= CURRENT_DATE" +
                        ")")
        List<Movie> getMoviesHaveSchedules();

        @Query("SELECT m FROM Movie m " +
                        "WHERE m.movie_status = 0 " +
                        "AND m.release_date <= CURRENT_DATE " +
                        "AND EXISTS (" +
                        "    SELECT 1 " +
                        "    FROM m.movie_Room_Dates rd " +
                        "    GROUP BY rd.movie " +
                        "    HAVING MAX(rd.movie_date) >= CURRENT_DATE" +
                        ")")
        public List<Movie> findAllCurrentMovie();

        @Query("select u from Movie u order by u.movie_id desc limit 1")
        public Movie getTop();

        @Query("select u from Movie u where u.movie_id = ?1")
        public Movie getMovieById(Long id);
}
