package com.group5.cpl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.group5.cpl.model.Schedule;
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long>{
    @Query("SELECT g from Schedule g WHERE g.value = ?1")
    Schedule findByValue(String value);
}
