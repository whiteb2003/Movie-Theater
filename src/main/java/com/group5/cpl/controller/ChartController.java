package com.group5.cpl.controller;

import com.group5.cpl.model.dto.TicketCountDto;
import com.group5.cpl.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChartController {
    @Autowired
    TicketService ticketService;
    @GetMapping("/chart")
    public String chart(Model model){
        List<TicketCountDto> ticketCountDtos = ticketService.getTicketNumberNotCanceledNotDropped();
        model.addAttribute("datalist", ticketCountDtos);
        return "chart";
    }
}
