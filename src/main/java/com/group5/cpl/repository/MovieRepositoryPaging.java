package com.group5.cpl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.group5.cpl.model.Movie;

public interface MovieRepositoryPaging extends PagingAndSortingRepository<Movie, Long> {
        @Query("SELECT m FROM Movie m WHERE CONCAT(m.movie_title_en, ' ', m.movie_title_vn, ' ', m.actors, ' ', m.directors) LIKE %?1% order by m.movie_id desc")
        public Page<Movie> findAll(String keyword, Pageable pageable);

        @Query("SELECT m FROM Movie m WHERE m.movie_status = 0 AND CONCAT(m.movie_title_en, ' ', m.movie_title_vn, ' ', m.actors, ' ', m.directors) LIKE %?1% order by m.movie_id desc")
        public Page<Movie> findAllActive(String keyword, Pageable pageable);

        @Query("SELECT m FROM Movie m WHERE m.movie_status = 0 order by m.movie_id desc")
        public Page<Movie> findAllActive(Pageable pageable);

        @Query("SELECT m FROM Movie m WHERE m.movie_status = 1 AND CONCAT(m.movie_title_en, ' ', m.movie_title_vn, ' ', m.actors, ' ', m.directors) LIKE %?1% order by m.movie_id desc")
        public Page<Movie> findAllHidden(String keyword, Pageable pageable);

        @Query("SELECT m FROM Movie m WHERE m.movie_status = 1 order by m.movie_id desc")
        public Page<Movie> findAllHidden(Pageable pageable);

        @Query("SELECT m FROM Movie m " +
                        "WHERE m.movie_status = 0 " +
                        "AND m.release_date > CURRENT_DATE " +
                        "AND CONCAT(m.movie_title_en, ' ', m.movie_title_vn, ' ', m.actors, ' ', m.directors) LIKE %?1%"
                        +
                        "AND EXISTS (" +
                        "    SELECT 1 " +
                        "    FROM m.movie_Room_Dates rd " +
                        "    GROUP BY rd.movie " +
                        "    HAVING MAX(rd.movie_date) >= CURRENT_DATE" +
                        ") order by m.movie_id desc")
        public Page<Movie> findAllActiveHaveDateLarger(String keyword, Pageable pageable);

        @Query("SELECT m FROM Movie m " +
                        "WHERE m.movie_status = 0 " +
                        "AND m.release_date > CURRENT_DATE " +
                        "AND EXISTS (" +
                        "    SELECT 1 " +
                        "    FROM m.movie_Room_Dates rd " +
                        "    GROUP BY rd.movie " +
                        "    HAVING MAX(rd.movie_date) >= CURRENT_DATE" +
                        ") order by m.movie_id desc")
        public Page<Movie> findAllActiveHaveDateLarger(Pageable pageable);

        @Query("SELECT m FROM Movie m " +
                        "WHERE m.movie_status = 0 " +
                        "AND m.release_date <= CURRENT_DATE " +
                        "AND CONCAT(m.movie_title_en, ' ', m.movie_title_vn, ' ', m.actors, ' ', m.directors) LIKE %?1%"
                        +
                        "AND EXISTS (" +
                        "    SELECT 1 " +
                        "    FROM m.movie_Room_Dates rd " +
                        "    GROUP BY rd.movie " +
                        "    HAVING MAX(rd.movie_date) >= CURRENT_DATE" +
                        ") order by m.movie_id desc")
        public Page<Movie> findAllActiveHaveDateLess(String keyword, Pageable pageable);

        @Query("SELECT m FROM Movie m " +
                        "WHERE m.movie_status = 0 " +
                        "AND m.release_date <= CURRENT_DATE " +
                        "AND EXISTS (" +
                        "    SELECT 1 " +
                        "    FROM m.movie_Room_Dates rd " +
                        "    GROUP BY rd.movie " +
                        "    HAVING MAX(rd.movie_date) >= CURRENT_DATE" +
                        ") order by m.movie_id desc")
        public Page<Movie> findAllActiveHaveDateLess(Pageable pageable);

}
