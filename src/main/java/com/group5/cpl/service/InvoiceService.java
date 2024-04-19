package com.group5.cpl.service;

import com.group5.cpl.model.Invoice;
import com.group5.cpl.model.Promotion;
import com.group5.cpl.model.Ticket;
import com.group5.cpl.model.dto.InvoiceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvoiceService {

    public Invoice confirmInvoice(Long invoiceId, Long promotionId, List<Long> listTicketIds);

    public Invoice applyPromo(Long invoiceId, Long promotionId, List<Long> listTicketIds);

    Invoice addInvoice(Invoice invoice);

    public Invoice updateTicketList(Invoice invoice, List<Ticket> ticketList);

    public List<InvoiceResponseDto> invoiceResponseListConverter(Page<Invoice> invoicePage);

    public Page<Invoice> findListInvoicesByUserId(Long userId, Pageable pageable);

    public Invoice getInvoiceByID(long invoiceId);

    public Invoice findInvoiceInfoByUserId(Long userId, Long invoiceId);

    public Page<Invoice> findInvoiceByCode(String code, Pageable pageable);

    public InvoiceResponseDto singleInvoiceResponseConverter(Invoice item);

    public List<Ticket> getPaidTickets(Invoice invoice);

    List<Invoice> getListInvoiceByUserId(Long id,String code, Double price);

}
