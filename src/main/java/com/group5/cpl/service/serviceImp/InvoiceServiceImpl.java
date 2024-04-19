package com.group5.cpl.service.serviceImp;

import com.group5.cpl.model.Invoice;
import com.group5.cpl.model.Promotion;
import com.group5.cpl.model.Ticket;
import com.group5.cpl.model.dto.InvoiceResponseDto;
import com.group5.cpl.model.enums.InvoiceStatus;
import com.group5.cpl.model.enums.TicketStatus;
import com.group5.cpl.repository.InvoiceRepository;
import com.group5.cpl.repository.PromotionRepository;
import com.group5.cpl.service.AccountService;
import com.group5.cpl.service.InvoiceService;
import com.group5.cpl.service.TicketService;
import com.group5.cpl.utils.Invoice_Price_Util;
import com.group5.cpl.utils.Movie_Duration_Formatter;
import com.group5.cpl.utils.Promotion_Status_Util;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired  private PromotionRepository promotionRepository;
    @Autowired  private AccountService accountService;
    @Autowired private Invoice_Price_Util invoice_price_util;
    @Autowired private TicketService ticketService;
    @Autowired private Promotion_Status_Util promotion_status_util;

    @Override
    public Invoice addInvoice(Invoice invoice){
        return invoiceRepository.save(invoice);
    }


    @Override
    public Invoice updateTicketList(Invoice invoice, List<Ticket> ticketList){
        List<Ticket> allTickets = invoice.getTicketList();
        for(Ticket item : allTickets){
            if(ticketList.contains(item)){
                item.setStatus(TicketStatus.PAID);
            }else{
                item.setStatus(TicketStatus.CANCEL);
            }
//            ticketService.saveTicket(item);
        }
        return invoiceRepository.saveAndFlush(invoice);
    }


    @Override
    public Invoice applyPromo(Long invoiceId, Long promotionId, List<Long> listTicketIds){
        // Check if the invoice with the specified ID exists
        Invoice existingInvoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice with id " + invoiceId + " not found"));

        // Get the promotion with the specified ID
        Promotion promotionToAdd = new Promotion();
        if(promotionId == -1){
            promotionToAdd = null;
        }else {
            promotionToAdd = promotionRepository.findById(promotionId)
                    .orElseThrow(() -> new EntityNotFoundException("Promotion with id " + promotionId + " not found"));
            promotionToAdd.setStatus(promotion_status_util.promotionStatusChecker(promotionToAdd, promotionToAdd.getStatus()));
            promotionRepository.save(promotionToAdd);
        }

        //Get checked tickets to add
        List<Ticket> ticketsToAdd = ticketService.getTicketListByIds(listTicketIds);
        existingInvoice = updateTicketList(existingInvoice, ticketsToAdd);
        existingInvoice.setPromotion(promotionToAdd);
        existingInvoice.setPrice(invoice_price_util.paidTicketsCollector(existingInvoice));
        return invoiceRepository.saveAndFlush(existingInvoice);
    }

    @Override
    public Invoice confirmInvoice(Long invoiceId, Long promotionId, List<Long> listTicketIds){
        Invoice finalPromo = applyPromo(invoiceId, promotionId, listTicketIds);
        finalPromo.setStatus(InvoiceStatus.PAID);
        // Save and flush the updated invoice
        return invoiceRepository.saveAndFlush(finalPromo);
    }

    @Override
    public List<InvoiceResponseDto> invoiceResponseListConverter(Page<Invoice> invoicesPage){
        Movie_Duration_Formatter mrd = new Movie_Duration_Formatter();
        List<InvoiceResponseDto> responses = new ArrayList<>();
        for (Invoice item : invoicesPage) {
//            InvoiceResponseDto dto = InvoiceResponseDto.builder()
//                    .id(item.getInvoice_id())
//                    .movie_title_en(item.getTicketList().get(0).getMovie_schedule().getMrd_item().getMovie().getMovie_title_en())
//                    .movie_date(item.getTicketList().get(0).getMovie_schedule().getMrd_item().getMovie_date())
//                    .schedule(mrd.movieTime_24H(item.getTicketList().get(0).getMovie_schedule().getSchedule().getValue()))
//                    .room(item.getTicketList().get(0).getMovie_schedule().getMrd_item().getRoom().getRoom_name())
//                    .username(accountService.findUserById(item.getUser_id()).getUsername())
//                    .code(item.getCode())
//                    .status(item.getStatus())
//                    .price(invoice_price_util.priceToStringConverter(item.getPrice()))
//                    .build();
            InvoiceResponseDto dto = singleInvoiceResponseConverter(item);
            responses.add(dto);
        }
        return responses;
    }

    @Override
    public InvoiceResponseDto singleInvoiceResponseConverter(Invoice item){
        Movie_Duration_Formatter mrd = new Movie_Duration_Formatter();
            InvoiceResponseDto dto = InvoiceResponseDto.builder()
                    .id(item.getInvoice_id())
                    .movie_title_en(item.getTicketList().get(0).getMovie_schedule().getMrd_item().getMovie().getMovie_title_en())
                    .movie_date(item.getTicketList().get(0).getMovie_schedule().getMrd_item().getMovie_date())
                    .schedule(mrd.movieTime_24H(item.getTicketList().get(0).getMovie_schedule().getSchedule().getValue()))
                    .room(item.getTicketList().get(0).getMovie_schedule().getMrd_item().getRoom().getRoom_name())
                    .username(accountService.findUserById(item.getUser_id()).getUsername())
                    .code(item.getCode())
                    .status(item.getStatus())
                    .price(invoice_price_util.priceToStringConverter(item.getPrice()))
                    .usePoint(item.getUsePoint())
                    .build();
        return dto;
    }

    @Override
    public List<Ticket> getPaidTickets(Invoice invoice) {
        List<Ticket> allTickets = invoice.getTicketList();
        List<Ticket> paidTickets = new ArrayList<>();
        for (Ticket item : allTickets) {
            if (item.getStatus().equals(TicketStatus.PAID)) {
                paidTickets.add(item);
            }
        }
        return paidTickets;
    }

    public List<Invoice> getListInvoiceByUserId(Long id, String code, Double price) {
        List<Invoice> invoices = invoiceRepository.getListInvoice(id, code, price);
        return invoices;

    }

    @Override
    public Invoice getInvoiceByID(long invoiceId) {
        return invoiceRepository.findById(invoiceId).get();
    }

    @Override
    public Page<Invoice> findListInvoicesByUserId(Long userId, Pageable pageable) {
        return invoiceRepository.findListInvoicesByUserId(userId, pageable);
    }

    @Override
    public Invoice findInvoiceInfoByUserId(Long userId, Long invoiceId) {
        return invoiceRepository.findInvoiceInfoByUserId(userId, invoiceId);
    }

    @Override
    public Page<Invoice> findInvoiceByCode(String code, Pageable pageable){
        return invoiceRepository.findInvoicesByCode(code, pageable);
    }
}
