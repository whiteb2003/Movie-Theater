package com.group5.cpl.repository;

import java.sql.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.group5.cpl.model.Ticket;

public interface TicketRepositoryPaging extends PagingAndSortingRepository<Ticket, Long> {
        @Query("SELECT s FROM Ticket s WHERE " +
                        "(:userId IS NULL OR s.invoice.user_id = :userId) " +
                        "AND (:movieNameEn IS NULL OR :movieNameEn = '' OR s.movie_schedule.mrd_item.movie.movie_title_en like %:movieNameEn%) "
                        +
                        "AND (:movieDate IS NULL OR s.movie_schedule.mrd_item.movie_date = :movieDate) " +
                        "AND (:schedule IS NULL OR :schedule = 0 OR s.movie_schedule.schedule.value = :schedule) " +
                        "AND (:seatPosition IS NULL OR :seatPosition = '' OR s.seat.position = :seatPosition)")

        Page<Ticket> searchTicket(@Param("userId") Long userId,
                        @Param("movieNameEn") String movieNameEn,
                        @Param("movieDate") Date movieDate,
                        @Param("schedule") int schedule,
                        @Param("seatPosition") String seatPosition, Pageable pageable);

        @Query("SELECT s FROM Ticket s")
        Page<Ticket> searchTicket(Pageable pageable);
        @Query("SELECT s FROM Ticket s " +
                "WHERE (s.invoice.user_id = :userId) " +
                "AND (:movieNameEn IS NULL OR :movieNameEn = '' OR s.movie_schedule.mrd_item.movie.movie_title_en " +
                "LIKE CONCAT('%', :movieNameEn, '%')) " +
                "AND (:movieDate IS NULL OR s.movie_schedule.mrd_item.movie_date = :movieDate) " +
                "AND (:schedule IS NULL OR :schedule = 0 OR s.movie_schedule.schedule.value = :schedule) " +
                "AND (:seatPosition IS NULL OR :seatPosition = '' OR s.seat.position LIKE CONCAT('%', :seatPosition, '%'))" )
        Page<Ticket> searchAllTicketOfUser(@Param("userId") Long userId,
                                           @Param("movieNameEn") String movieNameEn,
                                           @Param("movieDate") Date movieDate,
                                           @Param("schedule") int schedule,
                                           @Param("seatPosition") String seatPosition, Pageable pageable);

        @Query("SELECT s FROM Ticket s  " +
                "WHERE (s.invoice.user_id = :userId) " +
                "AND (:movieNameEn IS NULL OR :movieNameEn = '' OR s.movie_schedule.mrd_item.movie.movie_title_en LIKE CONCAT('%', :movieNameEn, '%')) "
                +
                "AND (:movieDate IS NULL OR s.movie_schedule.mrd_item.movie_date = :movieDate) " +
                "AND (:schedule IS NULL OR :schedule = 0 OR s.movie_schedule.schedule.value = :schedule) " +
                "AND (:seatPosition IS NULL OR :seatPosition = '' OR s.seat.position LIKE CONCAT('%', :seatPosition, '%')) " +
                "AND s.status = 0")
        Page<Ticket> searchTicketBooking(@Param("userId") Long userId,
                                         @Param("movieNameEn") String movieNameEn,
                                         @Param("movieDate") Date movieDate,
                                         @Param("schedule") int schedule,
                                         @Param("seatPosition") String seatPosition, Pageable pageable);

        @Query("SELECT s FROM Ticket s  " +
                "WHERE (s.invoice.user_id = :userId) " +
                "AND (:movieNameEn IS NULL OR :movieNameEn = '' OR s.movie_schedule.mrd_item.movie.movie_title_en LIKE CONCAT('%', :movieNameEn, '%')) "
                +
                "AND (:movieDate IS NULL OR s.movie_schedule.mrd_item.movie_date = :movieDate) " +
                "AND (:schedule IS NULL OR :schedule = 0 OR s.movie_schedule.schedule.value = :schedule) " +
                "AND (:seatPosition IS NULL OR :seatPosition = '' OR s.seat.position LIKE CONCAT('%', :seatPosition, '%')) " +
                "AND s.status = 2")
        Page<Ticket> searchTicketBooked(@Param("userId") Long userId,
                                        @Param("movieNameEn") String movieNameEn,
                                        @Param("movieDate") Date movieDate,
                                        @Param("schedule") int schedule,
                                        @Param("seatPosition") String seatPosition, Pageable pageable);

        @Query("SELECT s FROM Ticket s " +
                "WHERE (s.invoice.user_id = :userId) " +
                "AND (:movieNameEn IS NULL OR :movieNameEn = '' OR s.movie_schedule.mrd_item.movie.movie_title_en LIKE CONCAT('%', :movieNameEn, '%')) "
                +
                "AND (:movieDate IS NULL OR s.movie_schedule.mrd_item.movie_date = :movieDate) " +
                "AND (:schedule IS NULL OR :schedule = 0 OR s.movie_schedule.schedule.value = :schedule) " +
                "AND (:seatPosition IS NULL OR :seatPosition = '' OR s.seat.position LIKE CONCAT('%', :seatPosition, '%')) " +
                "AND (s.status = 1 or s.status = 3)")
        Page<Ticket> searchTicketCancel(@Param("userId") Long userId,
                                        @Param("movieNameEn") String movieNameEn,
                                        @Param("movieDate") Date movieDate,
                                        @Param("schedule") int schedule,
                                        @Param("seatPosition") String seatPosition, Pageable pageable);

}
