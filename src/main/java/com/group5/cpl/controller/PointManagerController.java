package com.group5.cpl.controller;

import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.Invoice;
import com.group5.cpl.model.dto.InvoiceScoreDTO;
import com.group5.cpl.model.MyUserDetail;
import com.group5.cpl.service.AccountService;
import com.group5.cpl.service.InvoiceService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PointManagerController {
    private final AccountService accountService;
    private final InvoiceService invoiceService;

    public PointManagerController(AccountService accountService, InvoiceService invoiceService) {
        this.accountService = accountService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/score_search/{id}")
    public String getListScore(Authentication authentication, Model model,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "price", required = false) Double price) {
        List<Invoice> invoices = new ArrayList<>();
        List<InvoiceScoreDTO> invoiceScoreDTOS = new ArrayList<>();
        if (authentication != null && authentication.isAuthenticated()) {
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
            double score = accountService.findUserById(userDetails.getId()).getScore();
            AccountEntity accountEntity = accountService.findUserByUserName(userDetails.getUsername());
            if (accountEntity != null) {
                // Kiểm tra xem price có phải là số hoặc không trước khi tìm kiếm
                if (price != null && price % 1 == 0) { // Kiểm tra xem price có phải là số nguyên hay không
                    invoices = invoiceService.getListInvoiceByUserId(accountEntity.getAccount_id(), code, price);
                } else {
                    // Nếu price không phải là số nguyên hoặc không có, thực hiện tìm kiếm mà không
                    // có điều kiện về price
                    invoices = invoiceService.getListInvoiceByUserId(accountEntity.getAccount_id(), code, null);
                }
            }
            for (Invoice invoice : invoices) {
                InvoiceScoreDTO invoiceScoreDTO = new InvoiceScoreDTO();
                invoiceScoreDTO.setCode(invoice.getCode());
                invoiceScoreDTO.setPrice(invoice.getPrice());
                invoiceScoreDTO.setName(accountEntity.getFullname());
                invoiceScoreDTO.setUserPoint(invoice.getUsePoint());
                if (!invoice.getStatus().equals("UNPAID")) {
                    if (invoice.getUsePoint().equals(Boolean.TRUE)) {
                        invoiceScoreDTO.setPoints(invoice.getPrice() / 1000);
                    } else if (invoice.getUsePoint().equals(Boolean.FALSE) && invoice.getPromotion() != null) {
                        double temp = 0;
                        invoiceScoreDTO.setPoints(temp);
                    } else if (invoice.getUsePoint().equals(Boolean.FALSE)) {
                        invoiceScoreDTO.setPoints(invoice.getPrice() * 10 / 100 / 1000);
                    }
                } else {
                    invoiceScoreDTO.setPoints(null);
                }
                invoiceScoreDTO.setStatus(invoice.getStatus().toString());
                invoiceScoreDTO.setQr(invoice.getQr());
                invoiceScoreDTOS.add(invoiceScoreDTO);
            }
            model.addAttribute("invoiceScoreDTOS", invoiceScoreDTOS);
            model.addAttribute("totalScore", score);
            model.addAttribute("price", price);
            model.addAttribute("code", code);
        }
        return "Point";
    }

    @GetMapping("/score/{id}")
    public String showListInvoice(Authentication authentication,
            @PathVariable("id") long id) {
        return "redirect:/score_search/" + id;
    }

}
