package com.group5.cpl.controller;

import com.group5.cpl.model.Cinema_Room;
import com.group5.cpl.model.Seat;
import com.group5.cpl.model.Ticket;
import com.group5.cpl.model.enums.SeatType;
import com.group5.cpl.service.CinemaRoomService;
import com.group5.cpl.service.SeatService;
import com.group5.cpl.service.TicketService;
import com.group5.cpl.utils.Account_Util;
import com.group5.cpl.utils.Movie_Seat_Formatter;
import com.group5.cpl.utils.Seat_to_2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CinemaRoomManagerController {
    @Autowired
    private CinemaRoomService cinemaRoomService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private Account_Util account_util;

    @GetMapping("/addCinemaRoom")
    public String addCinemaRoom(Model model, @ModelAttribute("message") String message,
            @ModelAttribute("name") String name, @ModelAttribute("row") String row_raw,
            @ModelAttribute("column") String column_raw) {
        Cinema_Room cinemaRoom = new Cinema_Room();
        cinemaRoom.setRoom_name(name);
        if (row_raw != null && !row_raw.equals("")) {
            cinemaRoom.setSeat_row(Integer.parseInt(row_raw));
        }
        if (column_raw != null && !column_raw.equals("")) {
            cinemaRoom.setSeat_column(Integer.parseInt(column_raw));
        }
        model.addAttribute("Cinema_Room", cinemaRoom);
        model.addAttribute("message", message);
        return "AddCinemaRoom";
    }

    @PostMapping("/addCinemaRoom")
    public String addCinemaRoom(@ModelAttribute Cinema_Room cinemaRoom, Model model,
            RedirectAttributes redirectAttributes) {
        String message = "Added Successfully";
        try {
            if (cinemaRoom.getRoom_name().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("message",
                        "Room name must not be empty !!!");
                redirectAttributes.addFlashAttribute("row", cinemaRoom.getSeat_row());
                redirectAttributes.addFlashAttribute("column", cinemaRoom.getSeat_column());
                return "redirect:/addCinemaRoom";
            }
            if (cinemaRoomExist(cinemaRoom.getRoom_name())) {
                redirectAttributes.addFlashAttribute("message",
                        "Room " + cinemaRoom.getRoom_name() + " already exist!!!");
                redirectAttributes.addFlashAttribute("name", cinemaRoom.getRoom_name());
                redirectAttributes.addFlashAttribute("row", cinemaRoom.getSeat_row());
                redirectAttributes.addFlashAttribute("column", cinemaRoom.getSeat_column());
                return "redirect:/addCinemaRoom";
            }
            if (cinemaRoom.getSeat_column() < 0 || cinemaRoom.getSeat_row() < 0 ||
                    cinemaRoom.getSeat_column() > 12 || cinemaRoom.getSeat_column() > 12) {
                redirectAttributes.addFlashAttribute("message",
                        "Seats row and column have to be greater than 0 and smaller than 12");
                redirectAttributes.addFlashAttribute("name", cinemaRoom.getRoom_name());
                return "redirect:/addCinemaRoom";
            }
            int seatQuantity = cinemaRoom.getSeat_column() * cinemaRoom.getSeat_row();
            if (seatQuantity == 0) {
                throw new Exception();
            }
            Movie_Seat_Formatter mvs = new Movie_Seat_Formatter();
            cinemaRoom.setSeat_quantity(seatQuantity);
            cinemaRoom = cinemaRoomService.addCinemaRoom(cinemaRoom);
            for (int i = 0; i < seatQuantity; i++) {
                Seat seat = new Seat();
                seat.setRoom(cinemaRoom);
                seat.setStatus(true);
                seat.setType(SeatType.NORMAL);
                seat.setPosition(mvs.formatSeats(cinemaRoom.getSeat_row(), cinemaRoom.getSeat_column(), i));
                seatService.addSeat(seat);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("row", cinemaRoom.getSeat_row());
            redirectAttributes.addFlashAttribute("column", cinemaRoom.getSeat_column());
            redirectAttributes.addFlashAttribute("name", cinemaRoom.getRoom_name());
            redirectAttributes.addFlashAttribute("message",
                    "Name, seat rows and columns must not be empty");
            return "redirect:/addCinemaRoom";
        }
        redirectAttributes.addFlashAttribute("message", message);
        Long id = cinemaRoom.getRoom_id();
        return "redirect:/seat/seatsConfig/" + id;
    }

    @RequestMapping("/cinemaroom/{id}")
    public Cinema_Room getCinemaRoomByID(@PathVariable("id") long id) {
        return cinemaRoomService.getCinemaRoomByID(id);
    }

    @RequestMapping("/cinemarooms")
    public String getCinemaRooms(Model model, @ModelAttribute("message") String message) {
        return "redirect:/cinemarooms/1";
    }

    @GetMapping("/cinemarooms/{page}")
    public String getCinemaRoomsPaging(Model model, @ModelAttribute("message") String message,
            @PathVariable("page") int page,
            @RequestParam(name = "name", required = false) String name, Authentication authentication) {
        if (message == null) {
            message = "";
        }
        Page<Cinema_Room> p = cinemaRoomService.searchByName(name, page);
        List<Cinema_Room> list = p.getContent();
        int totalPage = p.getTotalPages();
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("cinemaRoomList", list);
        model.addAttribute("message", message);
        model.addAttribute("isManager", account_util.isUserManager(authentication));
        return "CinemaRoomList";
    }

    @GetMapping("/updateCinemaRoom/{id}")
    public String updateCinemaRoom(Model model, @ModelAttribute("message") String message,
            @PathVariable("id") long id) {
        Cinema_Room cinemaRoom = cinemaRoomService.getCinemaRoomByID(id);
        model.addAttribute("cinemaRoom", cinemaRoom);
        model.addAttribute("message", message);
        return "UpdateCinemaRoom";
    }

    @PostMapping("/updateCinemaRoom/{id}")
    public String updateCinemaRoom(@ModelAttribute Cinema_Room cinemaRoom, Model model,
            RedirectAttributes redirectAttributes, @PathVariable("id") long id) {
        String message = "Update Successfully";
        try {
            Seat_to_2D st2 = new Seat_to_2D();
            Movie_Seat_Formatter mvs = new Movie_Seat_Formatter();
            if (cinemaRoom.getRoom_name().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("message",
                        "Room name must not be empty !!!");
                return "redirect:/updateCinemaRoom/" + id;
            }
            if (cinemaRoomExistForUpdate(cinemaRoom.getRoom_name(), id)) {
                redirectAttributes.addFlashAttribute("message",
                        "Room " + cinemaRoom.getRoom_name() + " already exist!!!");
                return "redirect:/updateCinemaRoom/" + id;
            }
            if (cinemaRoom.getSeat_column() < 0 || cinemaRoom.getSeat_row() < 0 ||
                    cinemaRoom.getSeat_column() > 12 || cinemaRoom.getSeat_row() > 12) {
                redirectAttributes.addFlashAttribute("message",
                        "Seats row and column have to be greater than 0  and smaller than 12");
                return "redirect:/updateCinemaRoom/" + id;
            }
            int seatQuantity = cinemaRoom.getSeat_column() * cinemaRoom.getSeat_row();
            if (seatQuantity == 0) {
                throw new Exception();
            }
            Cinema_Room oldcinemaRoom = cinemaRoomService.getCinemaRoomByID(id);

            // Handle when lower the column or row
            List<Seat> oldSeat = mvs.sortSeatsInARoom(oldcinemaRoom.getSeat_row(), oldcinemaRoom.getSeat_column(),
                    seatService.getSeatByRoomID(id));
            Seat_to_2D s = new Seat_to_2D();
            List<List<Seat>> oldSeat2D = s.convertTo2D(oldSeat, oldcinemaRoom.getSeat_row(),
                    oldcinemaRoom.getSeat_column());
            if (oldcinemaRoom.getSeat_row() >= cinemaRoom.getSeat_row() &&
                    oldcinemaRoom.getSeat_column() >= cinemaRoom.getSeat_column()) {

                // Handle if there a seat booked

                for (int i = 0; i < oldSeat2D.size(); i++) {
                    for (int j = 0; j < oldSeat2D.get(i).size(); j++) {
                        if (j >= cinemaRoom.getSeat_column() || i >= cinemaRoom.getSeat_row()) {
                            List<Ticket> ticket = ticketService.findTicketBySeatRoom(oldSeat2D.get(i).get(j));
                            if (ticket.size() > 0) {
                                redirectAttributes.addFlashAttribute("message",
                                        "Seats " + mvs.formatSeatsUsingCordinate(cinemaRoom.getSeat_row(),
                                                cinemaRoom.getSeat_column(), i, j) + " " +
                                                "having ticket booked so you can not delete it");
                                return "redirect:/updateCinemaRoom/" + id;
                            }
                        }
                    }
                }

                // Handle if there are not (allow to lower the room size)

                cinemaRoom.setSeat_quantity(seatQuantity);
                cinemaRoom.setRoom_id(id);
                cinemaRoom = cinemaRoomService.addCinemaRoom(cinemaRoom);

                for (int i = 0; i < oldSeat2D.size(); i++) {
                    for (int j = 0; j < oldSeat2D.get(i).size(); j++) {
                        if (j >= cinemaRoom.getSeat_column() || i >= cinemaRoom.getSeat_row()) {
                            seatService.deleteSeat(oldSeat2D.get(i).get(j).getId());
                        }
                    }
                }

            } else if (oldcinemaRoom.getSeat_row() <= cinemaRoom.getSeat_row() &&
                    oldcinemaRoom.getSeat_column() <= cinemaRoom.getSeat_column()) {

                // Handle when column or row are higher

                cinemaRoom.setSeat_quantity(seatQuantity);
                cinemaRoom.setRoom_id(id);
                cinemaRoom = cinemaRoomService.addCinemaRoom(cinemaRoom);

                for (int i = 0; i < cinemaRoom.getSeat_row(); i++) {
                    for (int j = 0; j < cinemaRoom.getSeat_column(); j++) {
                        if (j >= oldSeat2D.get(0).size() || i >= oldSeat2D.size()) {
                            // Add the new seat
                            Seat seat = new Seat();
                            seat.setRoom(cinemaRoom);
                            seat.setStatus(true);
                            seat.setType(SeatType.NORMAL);
                            seat.setPosition(mvs.formatSeatsUsingCordinate(cinemaRoom.getSeat_row(),
                                    cinemaRoom.getSeat_column(), i, j));
                            seatService.addSeat(seat);
                        }
                    }
                }
            } else if (oldcinemaRoom.getSeat_row() < cinemaRoom.getSeat_row() && // good
                    oldcinemaRoom.getSeat_column() > cinemaRoom.getSeat_column()) {

                // Handle if row higer but column lower (lower allowed)

                cinemaRoom.setSeat_quantity(seatQuantity);
                cinemaRoom.setRoom_id(id);
                cinemaRoom = cinemaRoomService.addCinemaRoom(cinemaRoom);
                for (int i = 0; i < cinemaRoom.getSeat_row(); i++) {
                    int col = 0;

                    if (i >= oldSeat2D.size()) {
                        col = cinemaRoom.getSeat_column();
                    } else {
                        col = oldSeat2D.get(0).size();
                    }

                    for (int j = 0; j < col; j++) {
                        if (i >= oldSeat2D.size()) {
                            // Add the new seat
                            Seat seat = new Seat();
                            seat.setRoom(cinemaRoom);
                            seat.setStatus(true);
                            seat.setType(SeatType.NORMAL);
                            seat.setPosition(mvs.formatSeatsUsingCordinate(cinemaRoom.getSeat_row(),
                                    cinemaRoom.getSeat_column(), i, j));
                            seatService.addSeat(seat);
                        }
                        if (j >= cinemaRoom.getSeat_column()) {
                            seatService.deleteSeat(oldSeat2D.get(i).get(j).getId());
                        }
                    }
                }
            } else if (oldcinemaRoom.getSeat_row() > cinemaRoom.getSeat_row() &&
                    oldcinemaRoom.getSeat_column() < cinemaRoom.getSeat_column()) {

                // Handle if column higher but row lower (lower allowed)

                cinemaRoom.setSeat_quantity(seatQuantity);
                cinemaRoom.setRoom_id(id);
                cinemaRoom = cinemaRoomService.addCinemaRoom(cinemaRoom);

                for (int i = 0; i < oldSeat2D.size(); i++) {
                    int col = 0;

                    if (i >= cinemaRoom.getSeat_row()) {
                        col = oldSeat2D.get(0).size();
                    } else {
                        col = cinemaRoom.getSeat_column();
                    }

                    for (int j = 0; j < col; j++) {
                        if (i >= cinemaRoom.getSeat_row()) {
                            // Delete excess seats in rows beyond new row count
                            seatService.deleteSeat(oldSeat2D.get(i).get(j).getId());
                        }
                        if (j >= oldSeat2D.get(0).size()) {
                            // Add new seats if column index exceeds old column count
                            Seat seat = new Seat();
                            seat.setRoom(cinemaRoom);
                            seat.setStatus(true);
                            seat.setType(SeatType.NORMAL);
                            seat.setPosition(mvs.formatSeatsUsingCordinate(cinemaRoom.getSeat_row(),
                                    cinemaRoom.getSeat_column(), i, j));
                            seatService.addSeat(seat);
                        }
                    }
                }
            }
            redirectAttributes.addFlashAttribute("message", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message",
                    "Name, seat rows and columns must not be empty");
            return "redirect:/updateCinemaRoom/" + id;
        }
        return "redirect:/seat/seatsConfig/" + id;
    }

    public boolean cinemaRoomExist(String name) {
        List<Cinema_Room> list = cinemaRoomService.getCinemaRooms();
        for (int i = 0; i < list.size(); i++) {
            if (name.equals(list.get(i).getRoom_name())) {
                return true;
            }
        }
        return false;
    }

    public boolean cinemaRoomExistForUpdate(String name, long id) {
        List<Cinema_Room> list = cinemaRoomService.getCinemaRooms();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getRoom_id() != id) {
                if (name.equals(list.get(i).getRoom_name())) {
                    return true;
                }
            }
        }
        return false;
    }
}
