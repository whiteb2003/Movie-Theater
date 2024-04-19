package com.group5.cpl.repository;

import com.group5.cpl.model.Promotion;
import com.group5.cpl.model.enums.PromotionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("select p from Promotion p where p.status = ?1")
    Page<Promotion> findAllByStatus(PromotionStatus status, Pageable pageable);

    @Query("select p from Promotion p where p.status = ?1")
    List<Promotion> findAllByStatus(PromotionStatus status);

    @Query("select p from Promotion p where p.title LIKE CONCAT('%', ?1 , '%') or ?1 is null or ?1 = ''")
    Page<Promotion> findAll(String title ,Pageable pageable);

    @Query("select p from Promotion p where p.endDate >= CURRENT_DATE and p.status = 'AVAILABLE'")
    List<Promotion> findAllActive();
}
