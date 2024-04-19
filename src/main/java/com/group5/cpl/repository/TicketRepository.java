package com.group5.cpl.repository;

import com.group5.cpl.model.Movie_Room_Date;
import com.group5.cpl.model.Seat;
import com.group5.cpl.model.Ticket;
import com.group5.cpl.model.dto.TicketCountDto;
import com.group5.cpl.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Ticket u SET u.movie_schedule.movie_schedule_id = null WHERE u.movie_schedule.movie_schedule_id = ?1")
    void update(Long id);

    // get ticket by id, user_id must be correct
    @Query("SELECT t FROM Ticket t WHERE t.ticket_id = ?1 and t.invoice.user_id = ?2")
    Optional<Ticket> findByTicket_idAndUser_id(Long ticket_id, Long user_id);

    // find tickets by user_id
    @Query("SELECT t FROM Ticket t WHERE t.invoice.user_id = ?1")
    Page<Ticket> findAllByUser_id(Long user_id, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.status = ?1")
    Page<Ticket> findAllByStatus(TicketStatus status, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.invoice.user_id = ?1 AND t.status = ?2")
    Page<Ticket> findAllByUser_idAndStatus(Long user_id, TicketStatus status, Pageable pageable);

    @Query("select t from Ticket t where t.seat = ?1")
    List<Ticket> findBySeat(Seat seat);

    @Query("SELECT t FROM Ticket t WHERE t.movie_schedule.movie_schedule_id = ?1 AND t.seat.id = ?2 AND (t.status = 2 or t.status = 0)")
    List<Ticket> findByScheduleANDSeatNotCanceledOrDropped(Long movie_scheduleID, Long seatID);

    @Query("SELECT t FROM Ticket t WHERE t.movie_schedule.movie_schedule_id = ?1 AND t.seat.id = ?2 AND (t.status = 2 or t.status = 1 or t.status = 0)")
    List<Ticket> findByScheduleANDSeatNotCanceled(Long movie_scheduleID, Long seatID);

    // find tickets by user with role manager
    @Query("SELECT t FROM Ticket t WHERE t.invoice.user_id IN :managerIds AND t.status = :status")
    Page<Ticket> findAllByUser_idInAndStatus(@Param("managerIds") List<Long> managerIds,
                                             @Param("status") TicketStatus status, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.invoice.user_id IN :managerIds")
    Page<Ticket> findAllByUser_idIn(@Param("managerIds") List<Long> managerIds, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.movie_schedule.movie_schedule_id = ?1")
    List<Ticket> findBySchedule(Long movie_scheduleID);

    @Query("SELECT t FROM Ticket t WHERE t.movie_schedule.movie_schedule_id = ?1 AND (t.status = 0 or t.status = 2)")
    List<Ticket> findByScheduleNotCanceled(Long movie_scheduleID);

    @Query("SELECT t.movie_schedule.mrd_item.movie.movie_title_en, count(t) as ticket_num " +
            "from Ticket t where (t.status = 0 or t.status = 2) group by t.movie_schedule.mrd_item.movie.movie_title_en")
    List<TicketCountDto> getTicketNumberNotCanceledNotDropped();
}
