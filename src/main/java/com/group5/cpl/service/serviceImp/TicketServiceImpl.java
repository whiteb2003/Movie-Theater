package com.group5.cpl.service.serviceImp;

import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.Seat;
import com.group5.cpl.model.Ticket;
import com.group5.cpl.model.dto.TicketCountDto;
import com.group5.cpl.model.dto.TicketDisplayAdminDto;
import com.group5.cpl.model.dto.TicketResponseDTO;
import com.group5.cpl.model.enums.TicketStatus;
import com.group5.cpl.repository.AccountRepository;
import com.group5.cpl.repository.TicketRepository;
import com.group5.cpl.repository.TicketRepositoryPaging;
import com.group5.cpl.service.InvoiceService;
import com.group5.cpl.service.TicketService;
import com.group5.cpl.utils.Account_Util;
import com.group5.cpl.utils.Invoice_Price_Util;
import com.group5.cpl.utils.Movie_Duration_Formatter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TicketRepositoryPaging ticketRepositoryPaging;

    @Override
    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicketByID(long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket with id " + ticketId + " not found"));
    }

    @Override
    public List<Ticket> getTicketListByIds(List<Long> ticketIds){
        List<Ticket> ticketList = new ArrayList<>();
        for(Long id: ticketIds){
            Ticket item = getTicketByID(id);
            ticketList.add(item);
        }
        return ticketList;
    }

    @Override
    public Ticket getTicketByIDAndUserID(long ticketId, long userId) {
        return ticketRepository.findByTicket_idAndUser_id(ticketId, userId).get();
    }

    @Override
    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }

    public Page<Ticket> searchTicketBooking(Long userId,
                                     String movie_name_en,
                                     Date movie_date,
                                     int schedule,
                                     String seat_position,
                                     int pageable) {
        Pageable page = PageRequest.of(pageable - 1, 9);
        return ticketRepositoryPaging.searchTicketBooking(userId,movie_name_en,movie_date,schedule,seat_position,page);

    }

    @Override
    public Page<Ticket> searchTicketBooked(Long userId,
                                    String movie_name_en,
                                    Date movie_date,
                                    int schedule,
                                    String seat_position,
                                    int pageable) {
        Pageable page = PageRequest.of(pageable - 1, 9);
        return ticketRepositoryPaging.searchTicketBooked(userId,movie_name_en,movie_date,schedule,seat_position,page);

    }

    public Page<Ticket> searchTicketCancel(Long userId,
                                     String movie_name_en,
                                     Date movie_date,
                                     int schedule,
                                     String seat_position,
                                     int pageable) {
        Pageable page = PageRequest.of(pageable - 1, 9);
        return ticketRepositoryPaging.searchTicketCancel(userId,movie_name_en,movie_date,schedule,seat_position,page);

    }

    public Page<Ticket> searchAllTicketOfUser(Long userId,
                                           String movie_name_en,
                                           Date movie_date,
                                           int schedule,
                                           String seat_position,
                                           int pageable) {
        Pageable page = PageRequest.of(pageable - 1, 9);
        return ticketRepositoryPaging.searchAllTicketOfUser(userId,movie_name_en,movie_date,schedule,seat_position,page);

    }

    // 0.1: sortBy is useless
    @Override
    public Page<Ticket> findTicketWithPaging(int offset, int pageSize, TicketStatus status) {
        Pageable pageable = PageRequest.of(offset, pageSize);
        Page<Ticket> tickets;
        if (status != null) {
            tickets = ticketRepository.findAllByStatus(status, pageable);
        } else {
            tickets = ticketRepository.findAll(pageable);
        }
        return tickets;
    }

    @Override
    public Page<Ticket> findTicketCreatedByManagerWithPaging(int offset, int pageSize, TicketStatus status) {
        List<AccountEntity> usersWithRoleManager = accountRepository.findByRoleEntity_Id(2L);
        List<Long> managerIds = usersWithRoleManager.stream()
                .map(AccountEntity::getAccount_id)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(offset, pageSize);
        Page<Ticket> tickets;
        if (status != null) {
            tickets = ticketRepository.findAllByUser_idInAndStatus(managerIds, status, pageable);
        } else {
            tickets = ticketRepository.findAllByUser_idIn(managerIds, pageable);
        }
        return tickets;
    }

    @Override
    public Page<Ticket> findTicketByUserIdWithPaging(Long user_id, int offset, int pageSize, TicketStatus status) {
        Pageable pageable = PageRequest.of(offset, pageSize);
        Page<Ticket> tickets;
        if (status != null) {
            tickets = ticketRepository.findAllByUser_idAndStatus(user_id, status, pageable);
        } else {
            tickets = ticketRepository.findAllByUser_id(user_id, pageable);
        }

        return tickets;
    }

    @Override
    public Ticket updateTicketStatus(Ticket ticket, TicketStatus status, boolean isManager) {
        if (isManager) { //manager
            if((status.equals(TicketStatus.PAID)) && (!ticket.getStatus().equals(TicketStatus.PAID))){
//                invoiceService.createInvoice(ticket);
            }
            ticket.setStatus(status);
            return ticketRepository.save(ticket);
        } else { //user
            if (status.equals(TicketStatus.PAID) || ticket.getStatus().equals(TicketStatus.PAID)
                    || ticket.getStatus().equals(TicketStatus.DROPPED)) {
                return ticket;
            } else { // ticket status is pending then change to dropped
                ticket.setStatus(status);
                if (status.equals(TicketStatus.PAID)) {

//                    invoiceService.createInvoice(ticket);
                }
                return ticketRepository.save(ticket);
            }
        }
    }

    @Override
    public Ticket changeTicketStatus(Ticket ticket, TicketStatus status) {
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    @Override
    public Page<Ticket> listAll(int pageNumber, Long userId, String movieNameEn, Date movieDate, int schedule,
            String seatPosition) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 4);
        return ticketRepositoryPaging.searchTicket(userId, movieNameEn, movieDate,
                schedule, seatPosition,
                pageable);
    }

    @Override
    public List<Ticket> findTicketBySeatRoom(Seat seat) {
        return ticketRepository.findBySeat(seat);
    }

    public List<TicketDisplayAdminDto> convertTicketsToDisplay(Page<Ticket> tickets, Authentication authentication) {
        Movie_Duration_Formatter mrd = new Movie_Duration_Formatter();
        Invoice_Price_Util price_util = new Invoice_Price_Util();
        Account_Util account_util =  new Account_Util();
        List<TicketDisplayAdminDto> ticketDisplay = new ArrayList<>();
        for(Ticket data : tickets){
            TicketDisplayAdminDto value = TicketDisplayAdminDto.builder()
                    .ticketID(data.getTicket_id())
                    .movie_name(data.getMovie_schedule().getMrd_item().getMovie().getMovie_title_en())
                    .movie_date(data.getMovie_schedule().getMrd_item().getMovie_date())
                    .movie_schedule(mrd.movieTime_24H(data.getMovie_schedule().getSchedule().getValue()))
                    .seat_position(data.getSeat().getPosition())
                    .seat_price(price_util.seatPriceCollector(data))
                    .customer_name(account_util.myUserDetails(authentication).getUsername())
                    .ticketStatus(data.getStatus())
                    .build();
            ticketDisplay.add(value);
        }
        return ticketDisplay;
    }

    @Override
    public List<Ticket> findByScheduleANDSeatNotCanceledOrDropped(long scheduleId, long seatId) {
        return ticketRepository.findByScheduleANDSeatNotCanceledOrDropped(scheduleId, seatId);
    }

    @Override
    public List<Ticket> findByScheduleANDSeatNotCanceled(long scheduleId, long seatId) {
        return ticketRepository.findByScheduleANDSeatNotCanceled(scheduleId, seatId);
    }

    @Override
    public List<Ticket> findByScheduleNotCancel(long movie_scheduleId) {
        return ticketRepository.findByScheduleNotCanceled(movie_scheduleId);
    }

    @Override
    public List<TicketCountDto> getTicketNumberNotCanceledNotDropped() {
        return ticketRepository.getTicketNumberNotCanceledNotDropped();
    }

}
