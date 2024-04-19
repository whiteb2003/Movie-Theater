package com.group5.cpl.controller;

import com.group5.cpl.model.Cinema_Room;
import com.group5.cpl.model.Seat;
import com.group5.cpl.model.dto.SeatsDto;
import com.group5.cpl.model.enums.SeatType;
import com.group5.cpl.service.CinemaRoomService;
import com.group5.cpl.service.SeatService;
import com.group5.cpl.utils.Movie_Seat_Formatter;
import com.group5.cpl.utils.Seat_to_2D;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/seat")
public class SeatController {
    @Autowired
    private SeatService seatService;
    @Autowired
    private CinemaRoomService cinemaRoomService;

    @GetMapping("/seatsConfig/{id}")
    public String SeatsConfig(Model model, @PathVariable("id") int cinemaRoomID) {
        model.addAttribute("seat", new Seat());
        Cinema_Room cinemaRoom = cinemaRoomService.getCinemaRoomByID(cinemaRoomID);
        Movie_Seat_Formatter msf = new Movie_Seat_Formatter();
        Seat_to_2D seattable = new Seat_to_2D();
        List<Seat> seatRoom = msf.sortSeatsInARoom(cinemaRoom.getSeat_row(), cinemaRoom.getSeat_column(),
                cinemaRoom.getSeatList());
        List<List<Seat>> seatRoom2D = seattable.convertTo2D(seatRoom, cinemaRoom.getSeat_row(),
                cinemaRoom.getSeat_column());
        model.addAttribute("seats", seatRoom2D);
        model.addAttribute("id", cinemaRoomID);
        return "SeatsConfig";
    }

    @PostMapping("/saveSeats/{id}")
    public String saveSeats(HttpServletRequest request,
            @ModelAttribute SeatsDto seats,
            RedirectAttributes redirectAttributes,
            @PathVariable("id") int room_id) {
        Cinema_Room cinemaRoom = cinemaRoomService.getCinemaRoomByID(room_id);
        String act = request.getParameter("act");
        String[] idSeat = request.getParameterValues("seat");
        Movie_Seat_Formatter msf = new Movie_Seat_Formatter();
        if (idSeat == null) {
            redirectAttributes.addFlashAttribute("error", "Please choose seat first!!!");
            return "redirect:/seat/seatsConfig/" + room_id;
        }

        List<String> seatId_raw = Arrays.asList(idSeat);
        List<Long> seatId = new ArrayList<>();
        for (int i = 0; i < seatId_raw.size(); i++) {
            seatId.add(Long.parseLong(seatId_raw.get(i)));
        }

        List<Seat> oldSeats = msf.sortSeatsInARoom(cinemaRoom.getSeat_row(), cinemaRoom.getSeat_column(),
                cinemaRoom.getSeatList());
        for (int i = 0; i < oldSeats.size(); i++) {
            if (seatId.contains(oldSeats.get(i).getId())) {
                if (act.equals("Set VIP")) {
                    oldSeats.get(i).setType(SeatType.VIP);
                } else if (act.equals("Set Normal")) {
                    oldSeats.get(i).setType(SeatType.NORMAL);
                } else if (act.equals("Set UNAVAILABLE")) {
                    oldSeats.get(i).setStatus(false);
                } else if (act.equals("Set AVAILABLE")) {
                    oldSeats.get(i).setStatus(true);
                }
            }
        }
        cinemaRoom.setSeatList(oldSeats);
        cinemaRoomService.addCinemaRoom(cinemaRoom);
        return "redirect:/seat/seatsConfig/" + room_id;
    }

}
