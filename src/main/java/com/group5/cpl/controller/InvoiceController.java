package com.group5.cpl.controller;

import com.group5.cpl.model.*;
import com.group5.cpl.model.dto.InvoiceResponseDto;

import com.group5.cpl.model.enums.InvoiceStatus;

import com.group5.cpl.repository.AccountRepository;
import com.group5.cpl.repository.InvoiceRepository;
import com.group5.cpl.service.AccountService;
import com.group5.cpl.service.InvoiceService;
import com.group5.cpl.service.PromotionService;
import com.group5.cpl.service.TicketService;
import com.group5.cpl.utils.Account_Util;
import com.group5.cpl.utils.Invoice_Price_Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequestMapping("/invoice")
@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final TicketService ticketService;
    private final PromotionService promotionService;
    private final AccountService accountService;
    private final Account_Util account_util;
    private final Invoice_Price_Util invoice_price_util;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    // Constructor injection of services
    public InvoiceController(InvoiceService invoiceService,
            TicketService ticketService,
            PromotionService promotionService,
            AccountService accountService,
            Account_Util account_util,
            Invoice_Price_Util invoice_price_util) {
        this.invoiceService = invoiceService;
        this.ticketService = ticketService;
        this.promotionService = promotionService;
        this.accountService = accountService;
        this.account_util = account_util;
        this.invoice_price_util = invoice_price_util;
    }

    @GetMapping("/list")
    public String getAllInvoices(Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "8") int pageSize,
            @RequestParam(defaultValue = "") String code) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            boolean isEmployee = account_util.isUserEmployee(authentication);
            Pageable pageable = (Pageable) PageRequest.of(offset, pageSize);
            Page<Invoice> invoicesPage;
//            if (isManager) {
                invoicesPage = invoiceService.findInvoiceByCode(code, pageable);
//            } else {
//                invoicesPage = invoiceService
//                        .findListInvoicesByUserId(account_util.myUserDetails(authentication).getId(), pageable);
//
//            }
            List<InvoiceResponseDto> responses = invoiceService.invoiceResponseListConverter(invoicesPage);

            model.addAttribute("code", code);
            model.addAttribute(" ", invoicesPage.getTotalElements());
            model.addAttribute("currentPage", invoicesPage.getNumber());
            model.addAttribute("totalPages", invoicesPage.getTotalPages());
            model.addAttribute("pageSize", invoicesPage.getSize());
            model.addAttribute("isManager", isManager);
            model.addAttribute("isEmployee", isEmployee);
            model.addAttribute("invoices", responses);
            return "invoice"; // Show invoice list for the user
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";
    }

    @GetMapping("/{invoiceId}")
    public String getInvoiceById(Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @PathVariable("invoiceId") Long invoiceId) {
        if (account_util.isAuthenticated(authentication)) {
            Invoice invoice = invoiceService.getInvoiceByID(invoiceId);
            if (invoice == null) {
                redirectAttributes.addFlashAttribute("error", "The page you are requesting is unavailable.");
                return "redirect:/invoice/list";
            }
            AccountEntity user = accountService
                    .findUserByUserName(account_util.myUserDetails(authentication).getUsername());
            // Check if the user has the role of MANAGER
            boolean isManager = (account_util.isUserManager(authentication) || account_util.isUserEmployee(authentication));
            if (isManager) {
                InvoiceResponseDto dto = invoiceService.singleInvoiceResponseConverter(invoice);
                List<Ticket> tickets = invoice.getTicketList();
                Promotion selectedPromotion = invoice.getPromotion();
                model.addAttribute("invoice", dto);
                model.addAttribute("tickets", tickets);
                model.addAttribute("selectedPromotion", selectedPromotion);
                model.addAttribute("isManager", isManager);
                return "invoiceDetails";
            } else {
                Invoice userInvoice = invoiceService.findInvoiceInfoByUserId(user.getAccount_id(), invoiceId);
                if (userInvoice != null) {
                    InvoiceResponseDto dto = invoiceService.singleInvoiceResponseConverter(userInvoice);
                    List<Ticket> tickets = invoiceService.getPaidTickets(userInvoice);
                    Promotion selectedPromotion = invoice.getPromotion();
                    model.addAttribute("invoice", dto);
                    model.addAttribute("tickets", tickets);
                    model.addAttribute("selectedPromotion", selectedPromotion);
                    model.addAttribute("isManager", isManager);
                    return "invoiceDetails";
                } else {
                    redirectAttributes.addFlashAttribute("error", "The page you are requesting is unavailable.");
                    return "redirect:/invoice/list"; // Invoice not found for the user, redirect to list
                }
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";
    }

    @GetMapping("/confirm/{invoiceId}")
    public String editInvoice(Model model,
            Authentication authentication,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @PathVariable("invoiceId") Long invoiceId) {
        if (account_util.isAuthenticated(authentication) && (account_util.isUserManager(authentication) || account_util.isUserEmployee(authentication))) {
            Invoice invoice = invoiceService.getInvoiceByID(invoiceId);
            if (invoice.getStatus().equals(InvoiceStatus.PAID)) {
                String code = invoice.getCode();
                if (code != null) {
                    redirectAttributes.addFlashAttribute("error",
                            "This invoice with code " + code + " has already been paid.");
                } else {
                    redirectAttributes.addFlashAttribute("error", "This invoice has already been paid.");
                }
                return "redirect:/invoice/list";
            } else {
                InvoiceResponseDto dto = invoiceService.singleInvoiceResponseConverter(invoice);
                List<Ticket> tickets = invoice.getTicketList();
                Long selectedPromotionId = invoice.getPromotion() != null ? invoice.getPromotion().getPromotion_id()
                        : -1L;
                List<Promotion> promotions = promotionService.getAllActivePromotions();
                model.addAttribute("invoice", dto);
                model.addAttribute("tickets", tickets);
                model.addAttribute("selectedPromotionId", selectedPromotionId);
                model.addAttribute("promotions", promotions);
                return "invoice_confirm";
            }

        }
        redirectAttributes.addFlashAttribute("error", "The page you are requesting is unavailable.");
        return "redirect:/home";
    }

    // Check price before confirm
    @PostMapping("/checkout")
    public String checkOut(Model model,
            Authentication authentication,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam("ticketIds") List<Long> ticketIds) {
        if (account_util.isAuthenticated(authentication)) {
            // Get promotionId and invoiceId from request parameter
            String promotionIdString = request.getParameter("promotionId");
            String invoiceIdString = request.getParameter("invoiceId");
            if (invoiceIdString == null || invoiceIdString.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Invoice ID is missing");
                return "redirect:/invoice/" + invoiceIdString + "";
            }
            if (promotionIdString == null || promotionIdString.isEmpty()) {
                promotionIdString = "-1";
            }

            try {
                Long promotionId = Long.parseLong(promotionIdString);
                Long invoiceId = Long.parseLong(invoiceIdString);

                Invoice checker = invoiceService.getInvoiceByID(invoiceId);
                if (checker.getStatus().equals(InvoiceStatus.PAID)) {
                    String code = checker.getCode();
                    if (code != null) {
                        redirectAttributes.addFlashAttribute("error",
                                "This invoice with code " + code + " has already been paid.");
                    } else {
                        redirectAttributes.addFlashAttribute("error", "This invoice has already been paid.");
                    }
                    return "redirect:/invoice/list";
                } else {
                    // Update invoice
                    Invoice invoice = invoiceService.applyPromo(invoiceId, promotionId, ticketIds);

                    if (invoice == null) {
                        redirectAttributes.addFlashAttribute("error", "Failed to fetch invoice");
                        return "redirect:/invoice/list";
                    }

                    InvoiceResponseDto dto = invoiceService.singleInvoiceResponseConverter(invoice);
                    List<Ticket> tickets = invoice.getTicketList();
                    Long selectedPromotionId = invoice.getPromotion() != null ? invoice.getPromotion().getPromotion_id()
                            : -1L;
                    List<Promotion> promotions = promotionService.getAllActivePromotions();
                    model.addAttribute("invoice", dto);
                    model.addAttribute("tickets", tickets);
                    model.addAttribute("selectedPromotionId", selectedPromotionId);
                    model.addAttribute("promotions", promotions);
                    return "invoice_confirm";
                    // return "redirect:/invoice/" + invoiceIdString + "";
                }

            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("error", "Invalid promotion ID or invoice ID format");
                return "redirect:/invoice/confirm/" + invoiceIdString + "";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";
    }

    // confirm invoice status to PAID
    @PostMapping("/confirm")
    public String confirmInvoice(Model model,
            Authentication authentication,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @RequestParam("ticketIds") List<Long> ticketIds,
            @RequestParam("usePoint") boolean usePoint) {
        if (account_util.isAuthenticated(authentication) && (account_util.isUserManager(authentication) || account_util.isUserEmployee(authentication))) {
            // Get promotionId and invoiceId from request parameter
            boolean isManager = (account_util.isUserManager(authentication) || account_util.isUserEmployee(authentication));
            String promotionIdString = request.getParameter("promotionId");
            String invoiceIdString = request.getParameter("invoiceId");

            if (invoiceIdString == null || invoiceIdString.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Invoice ID is missing");
                return "redirect:/invoice/" + invoiceIdString + "";
            }
            if (promotionIdString == null || promotionIdString.isEmpty()) {
                promotionIdString = "-1";
            }

            try {
                Long promotionId = Long.parseLong(promotionIdString);
                Long invoiceId = Long.parseLong(invoiceIdString);

                Invoice checker = invoiceService.getInvoiceByID(invoiceId);
                if (checker.getStatus().equals(InvoiceStatus.PAID)) {
                    String code = checker.getCode();
                    if (code != null) {
                        redirectAttributes.addFlashAttribute("error",
                                "This invoice with code " + code + " has already been paid.");
                    } else {
                        redirectAttributes.addFlashAttribute("error", "This invoice has already been paid.");
                    }
                    return "redirect:/invoice/list";
                } else {
                    // Update invoice
                    Invoice invoice = invoiceService.confirmInvoice(invoiceId, promotionId, ticketIds);

                    if (invoice == null) {
                        redirectAttributes.addFlashAttribute("error", "Failed to fetch invoice");
                        return "redirect:/invoice/list";
                    }
                    Double sum = invoice_price_util.paidTicketsCollector(invoice);
                    AccountEntity acc = accountService.findUserById(invoice.getUser_id());
                    if (usePoint) {
                        double r = acc.getScore() * 1000 - sum;
                        if (r < 0) {
                            acc.setScore(acc.getScore() + (sum / 10000));
                            invoiceRepository.saveAndFlush(invoice);
                            redirectAttributes.addFlashAttribute("error",
                                    "Member score is not enough to convert ticket");
                            return "redirect:/invoice/confirm/" + invoiceIdString + "";
                        } else {
                            acc.setScore(acc.getScore() - (sum / 1000));
                            invoice.setUsePoint(true);
                            invoiceRepository.saveAndFlush(invoice);
                            accountRepository.saveAndFlush(acc);
                        }
                    } else {
                        acc.setScore(acc.getScore() + (sum / 1000 * 0.1));
                        accountRepository.saveAndFlush(acc);
                    }

                    InvoiceResponseDto dto = invoiceService.singleInvoiceResponseConverter(invoice);
                    List<Ticket> tickets = invoice.getTicketList();
                    Long selectedPromotionId = invoice.getPromotion() != null ? invoice.getPromotion().getPromotion_id()
                            : -1L;
                    List<Promotion> promotions = promotionService.getAllActivePromotions();
                    model.addAttribute("invoice", dto);
                    model.addAttribute("tickets", tickets);
                    model.addAttribute("selectedPromotionId", selectedPromotionId);
                    model.addAttribute("promotions", promotions);
                    model.addAttribute("isManager", isManager);
//                    return "invoiceDetails";
//                    return getInvoiceById(model, authentication, redirectAttributes, invoiceId);
                     return "redirect:/invoice/" + invoiceIdString + "";
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("error", "Invalid ticket ID format");
                return "redirect:/invoice/list";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";

    }
}
