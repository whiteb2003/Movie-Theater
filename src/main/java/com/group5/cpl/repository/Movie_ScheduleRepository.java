package com.group5.cpl.repository;

import java.sql.Date;
import java.util.List;

import com.group5.cpl.model.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.group5.cpl.model.Movie_Schedule;
@Repository
public interface Movie_ScheduleRepository extends JpaRepository<Movie_Schedule,Long>{
    @Query("SELECT ms FROM Movie_Schedule ms WHERE ms.mrd_item.mrd_id = ?1")
    List<Movie_Schedule> getByMrdId(Long Id);

    @Transactional
    @Modifying
    @Query("DELETE FROM Movie_Schedule ms WHERE ms.mrd_item.mrd_id = ?1")
    void deleteByMrdId(Long mrd_id);

}
