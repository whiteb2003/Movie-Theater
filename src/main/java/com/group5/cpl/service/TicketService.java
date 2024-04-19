package com.group5.cpl.service;

import com.group5.cpl.model.Seat;
import com.group5.cpl.model.Ticket;
import com.group5.cpl.model.dto.TicketCountDto;
import com.group5.cpl.model.dto.TicketDisplayAdminDto;
import org.springframework.data.domain.Pageable;
import com.group5.cpl.model.dto.TicketResponseDTO;
import com.group5.cpl.model.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.sql.Date;
import java.util.List;

public interface TicketService {

    public Ticket saveTicket(Ticket ticket);

    public Ticket getTicketByID(long ticketId);

    public List<Ticket> getTicketListByIds(List<Long> ticketIds);

    public Ticket getTicketByIDAndUserID(long ticketId, long userId);

    public List<Ticket> getTickets();

    public Page<Ticket> searchTicketBooking(Long userId,
                                            String movie_name_en,
                                            Date movie_date,
                                            int schedule,
                                            String seat_position,
                                            int pageable);
    public Page<Ticket> searchTicketBooked(Long userId,
                                     String movie_name_en,
                                     Date movie_date,
                                     int schedule,
                                     String seat_position,
                                     int pageable);
    public Page<Ticket> searchTicketCancel(Long userId,
                                     String movie_name_en,
                                     Date movie_date,
                                     int schedule,
                                     String seat_position,
                                     int pageable);
    public Page<Ticket> searchAllTicketOfUser(Long userId,
                                           String movie_name_en,
                                           Date movie_date,
                                           int schedule,
                                           String seat_position,
                                           int pageable);

    public Page<Ticket> findTicketWithPaging(int offset, int pageSize, TicketStatus status);

    public Page<Ticket> findTicketByUserIdWithPaging(Long user_id, int offset, int pageSize,
            TicketStatus status);

    public Page<Ticket> findTicketCreatedByManagerWithPaging(int offset, int pageSize, TicketStatus status);

    public Ticket updateTicketStatus(Ticket ticket, TicketStatus status, boolean isManager);

    public Ticket changeTicketStatus(Ticket ticket, TicketStatus status);
    public Page<Ticket> listAll(int pageNumber, Long userId, String movieNameEn,
            Date movieDate,
            int schedule, String seatPosition);

    public List<Ticket> findTicketBySeatRoom(Seat seat);

    public List<TicketDisplayAdminDto> convertTicketsToDisplay(Page<Ticket> tickets, Authentication authentication);

    public List<Ticket> findByScheduleANDSeatNotCanceledOrDropped(long scheduleId, long seatId);

    public List<Ticket> findByScheduleANDSeatNotCanceled(long scheduleId, long seatId);

    public List<Ticket> findByScheduleNotCancel(long movie_scheduleId);

    public List<TicketCountDto> getTicketNumberNotCanceledNotDropped();
}
