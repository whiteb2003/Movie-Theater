package com.group5.cpl.controller;

import com.google.zxing.WriterException;
import com.group5.cpl.model.*;
import com.group5.cpl.model.dto.*;
import com.group5.cpl.model.enums.InvoiceStatus;
import com.group5.cpl.model.enums.TicketStatus;
import com.group5.cpl.repository.AccountRepository;
import com.group5.cpl.repository.InvoiceRepository;
import com.group5.cpl.repository.MovieRepository;
import com.group5.cpl.repository.Movie_Room_DateRepository;
import com.group5.cpl.repository.Movie_ScheduleRepository;
import com.group5.cpl.repository.PromotionRepository;
import com.group5.cpl.repository.SeatRepository;
import com.group5.cpl.repository.TicketRepository;
import com.group5.cpl.service.*;
import com.group5.cpl.utils.Account_Util;
import com.group5.cpl.utils.Movie_Duration_Formatter;
import com.group5.cpl.utils.Movie_Seat_Formatter;
import com.group5.cpl.utils.QRCodeGenerate;
import com.group5.cpl.utils.Seat_to_2D;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.mail.MessagingException;

@Controller
public class TicketController {
    @Autowired
    private Movie_Room_DateService movie_room_dateService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private Movie_ScheduleService movie_scheduleService;
    @Autowired
    private AccountService accountService;
    @Autowired
    TicketService ticketService;
    @Autowired
    CinemaRoomService cinemaRoomService;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    Account_Util account_util;
    @Autowired
    Movie_Room_DateRepository mDateRepository;
    @Autowired
    Movie_ScheduleRepository movie_ScheduleRepository;
    @Autowired
    PromotionRepository promotionRepository;
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    QRCodeGenerate qrCodeGenerate;
    @Autowired
    Movie_Duration_Formatter mdf;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int STRING_LENGTH = 8;

    public static String generateRandomString() {
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        Random random = new Random();

        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    @GetMapping("/datechose/{id}/{date}/{mrd_id}")
    public String dateChose(Authentication authentication, Model model,
            @PathVariable("id") long movieID,
            @PathVariable("date") java.sql.Date date,
            @PathVariable("mrd_id") long mrd_id) {
        Movie_Duration_Formatter mf = new Movie_Duration_Formatter();
        if (authentication != null && authentication.isAuthenticated()) {
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
            String user = userDetails.getUsername();
            AccountEntity u = accountService.findUserByUserName(user);
            Movie movie = movieService.findById(movieID);
            List<Movie_Room_Date> m = movie_room_dateService.getByMovieId(movieID);
            List<java.sql.Date> movie_room_dates = new ArrayList<>();
            for (int i = 0; i < m.size(); i++) {
                if (!movie_room_dates.contains(m.get(i).getMovie_date())) {
                    movie_room_dates.add(m.get(i).getMovie_date());
                }
            }

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Calendar current = Calendar.getInstance();
            List<java.sql.Date> dates = new ArrayList<>();
            for (int i = 0; i < movie_room_dates.size(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(movie_room_dates.get(i));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 59); // Set to 23:59:00
                calendar.set(Calendar.HOUR_OF_DAY, 23); // Set to 23:59:00
                calendar.set(Calendar.MILLISECOND, 0);

                String movieDateString = dateFormat.format(calendar.getTime());
                String currentDateString = dateFormat.format(current.getTime());

                try {
                    Date formattedMovieDate = dateFormat.parse(movieDateString);
                    Date formattedCurrentDate = dateFormat.parse(currentDateString);

                    if (formattedMovieDate.compareTo(formattedCurrentDate) >= 0) {
                        dates.add(movie_room_dates.get(i));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            Collections.sort(dates);
            List<Movie_Room_Date> mrd = movie_room_dateService.getByDateAndMovie(date, movieID);

            Collections.sort(mrd, new Comparator<Movie_Room_Date>() {
                @Override
                public int compare(Movie_Room_Date mrd1, Movie_Room_Date mrd2) {
                    // Compare by room
                    return mrd1.getRoom().getRoom_id().compareTo(mrd2.getRoom().getRoom_id());
                }
            });

            List<Movie_Schedule> movie_schedules = movie_scheduleService.getMovieSchedulebyMRDid(mrd_id);
            List<SchedulesDto> schedulesDtos = new ArrayList<>();
            for (int i = 0; i < movie_schedules.size(); i++) {
                SchedulesDto t = new SchedulesDto();
                t.setStart(mf.movieTime_24H(movie_schedules.get(i).getSchedule().getValue()));
                t.setEnd(mf.movieTime_24H(
                        movie_schedules.get(i).getSchedule().getValue() + Integer.parseInt(movie.getDuration()) + 30));
                t.setMovie_schedules_id(movie_schedules.get(i).getMovie_schedule_id());
                t.setRoom_name(movie_schedules.get(i).getMrd_item().getRoom().getRoom_name());
                t.setSeat(movie_schedules.get(i).getMrd_item().getRoom().getSeat_quantity());
                t.setSeat_booked(
                        ticketService.findByScheduleNotCancel(movie_schedules.get(i).getMovie_schedule_id()).size());
                schedulesDtos.add(t);
            }
            String duration = mf.durationformat(Integer.valueOf(movie.getDuration()));
            model.addAttribute("rooms", mrd);
            model.addAttribute("current_room", mrd_id);
            model.addAttribute("movie", movie);
            model.addAttribute("duration", duration);
            model.addAttribute("times", schedulesDtos);
            model.addAttribute("movie_room_dates", dates);
            model.addAttribute("current_id", date);
            model.addAttribute("userDetails", u);
        }
        return "movie_detail";
    }

    @GetMapping("/datechose/{id}/{date}")
    public String dateChose(Authentication authentication, Model model,
            @PathVariable("id") long movieID,
            @PathVariable("date") java.sql.Date date) {
        long mrd_id = movie_room_dateService.getByDateAndMovie(date, movieID).get(0).getMrd_id();
        return "redirect:/datechose/" + movieID + "/" + date + "/" + mrd_id;
    }

    @GetMapping("/datechose/{id}")
    public String roomChose(Authentication authentication, Model model, @PathVariable("id") long movieID) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar current = Calendar.getInstance();
        List<java.sql.Date> dates = new ArrayList<>();
        List<Movie_Room_Date> d = movie_room_dateService.getByMovieId(movieID);
        for (int i = 0; i < d.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d.get(i).getMovie_date());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 59); // Set to 23:59:00
            calendar.set(Calendar.HOUR_OF_DAY, 23); // Set to 23:59:00
            calendar.set(Calendar.MILLISECOND, 0);

            String movieDateString = dateFormat.format(calendar.getTime());
            String currentDateString = dateFormat.format(current.getTime());

            try {
                Date formattedMovieDate = dateFormat.parse(movieDateString);
                Date formattedCurrentDate = dateFormat.parse(currentDateString);

                if (formattedMovieDate.compareTo(formattedCurrentDate) >= 0) {
                    dates.add(d.get(i).getMovie_date());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        java.sql.Date date = dates.get(0);
        long mrd_id = movie_room_dateService.getByDateAndMovie(date, movieID).get(0).getMrd_id();
        return "redirect:/datechose/" + movieID + "/" + date + "/" + mrd_id;
    }

    @GetMapping("/seatchose/{id}/{movie_schedule_id}")
    public String dateChoseProcess(Authentication authentication, Model model,
            @PathVariable("id") long movieID,
            @PathVariable("movie_schedule_id") long movie_scheduleID,
            @ModelAttribute SeatsDto seatpick) {
        Seat_to_2D st2 = new Seat_to_2D();
        if (authentication != null && authentication.isAuthenticated()) {
            long mrdID = movie_scheduleService.getMovieSchedulesByID(movie_scheduleID).getMrd_item().getMrd_id();
            Calendar current = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            Date movieDate = movie_room_dateService.getByMRDid(mrdID).getMovie_date();
            int movieScheduleValue = movie_scheduleService.getMovieSchedulesByID(movie_scheduleID).getSchedule()
                    .getValue();
            calendar.setTime(movieDate);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.MINUTE, movieScheduleValue);

            String movieDateString = dateFormat.format(calendar.getTime());
            String currentDateString = dateFormat.format(current.getTime());

            try {
                Date formattedMovieDate = dateFormat.parse(movieDateString);
                Date formattedCurrentDate = dateFormat.parse(currentDateString);

                if (formattedMovieDate.compareTo(formattedCurrentDate) <= 0) {
                    return "redirect:/datechose/" + movieID + "/" + movieDate;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
            String user = userDetails.getUsername();
            AccountEntity u = accountService.findUserByUserName(user);
            Movie movie = movieService.findById(movieID);
            Movie_Room_Date mrd = movie_room_dateService.getByMRDid(mrdID);
            Cinema_Room room = mrd.getRoom();
            Movie_Seat_Formatter msf = new Movie_Seat_Formatter();
            List<Seat> seats = msf.sortSeatsInARoom(room.getSeat_row(), room.getSeat_column(), room.getSeatList());

            // check booked seat and temporary change status

            for (int i = 0; i < seats.size(); i++) {
                List<Ticket> tickets = ticketService.findByScheduleANDSeatNotCanceledOrDropped(movie_scheduleID,
                        seats.get(i).getId());
                if (tickets.size() > 0) {
                    seats.get(i).setStatus(false);
                }
            }

            Movie_Schedule ms = movie_scheduleService.getMovieSchedulesByID(movie_scheduleID);
            List<List<Seat>> seats_2D = st2.convertTo2D(seats, room.getSeat_row(), room.getSeat_column());
            model.addAttribute("seats", seats_2D);
            model.addAttribute("movie", movie);
            model.addAttribute("mrdID", mrdID);
            model.addAttribute("msID", movie_scheduleID);
            model.addAttribute("timeID", movie_scheduleID);
            model.addAttribute("mrdId", mrd.getMrd_id());
            model.addAttribute("userDetails", u);
            model.addAttribute("movieId", movieID);
        }
        return "select_seat";
    }

    @PostMapping("/chooseSeat")
    public String chooseSeat(HttpServletRequest request, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        String movieId = request.getParameter("movieId");
        String mrdID = request.getParameter("mrdID");
        String msID = request.getParameter("msID");
        String[] idSeat = request.getParameterValues("seat");
        if (idSeat == null) {
            redirectAttributes.addFlashAttribute("error", "Please choose seat!!!");
            return "redirect:/seatchose/" + movieId + "/" + msID;
        }
        List<String> seatId = Arrays.asList(idSeat);
        Optional<Movie> movie_o = movieRepository.findById(Long.parseLong(movieId));
        Movie movie = movie_o.get();
        Movie_Room_Date mrd = movie_room_dateService.getByMRDid(Long.parseLong(mrdID));
        Movie_Schedule ms = movie_scheduleService.getMovieSchedulesByID(Long.parseLong(msID));
        List<Seat> list = new ArrayList<>();

        for (String s : seatId) {
            list.add(seatRepository.getSeatById(Long.parseLong(s)));
        }
        double sum = 0.0;
        for (Seat seat : list) {
            sum += seat.getType().getPrice();
        }
        session.setAttribute("movie", movie);
        session.setAttribute("mrd", mrd);
        session.setAttribute("sum", sum);
        session.setAttribute("ms", ms);
        session.setAttribute("list", list);
        redirectAttributes.addFlashAttribute("back", "/seatchose/" + movieId + "/" + msID);
        return "redirect:/confirmByUser";
    }

    @GetMapping("/confirmByUser")
    public String displayTicketInformation(Model model, HttpSession session) {
        Movie movie = (Movie) session.getAttribute("movie");
        Movie_Room_Date mrd = (Movie_Room_Date) session.getAttribute("mrd");
        Movie_Schedule ms = (Movie_Schedule) session.getAttribute("ms");
        double sum = (double) session.getAttribute("sum");
        List<Seat> list = (List<Seat>) session.getAttribute("list");
        List<Promotion> promotions = promotionRepository.findAllActive();
        model.addAttribute("movie", movie);
        model.addAttribute("mrd", mrd);
        model.addAttribute("ms", ms);
        model.addAttribute("promotions", promotions);
        model.addAttribute("sum", sum);
        model.addAttribute("list", list);
        return "confirmByUser";
    }

    @PostMapping("/confirmTicket")
    public String confirm(Model model, HttpSession session, Authentication authentication,
            RedirectAttributes redirectAttributes, HttpServletRequest request)
            throws MessagingException, IOException, WriterException {
        Invoice invoice = new Invoice();
        String user1Id = request.getParameter("user1");
        String user2Id = request.getParameter("user2");
        String infor = request.getParameter("infor");
        Movie movie = (Movie) session.getAttribute("movie");
        String convert = request.getParameter("convert");
        String promotion = request.getParameter("promotion");
        AccountEntity acc;
        if (user2Id != null && !user2Id.isEmpty()) {
            user1Id = user2Id;
        }
        acc = accountService.findUserById(Long.parseLong(user1Id));
        if ((infor.isEmpty() || infor.equals("fail")) && (!acc.getRoleEntity().getName().equals("USER"))) {
            user1Id = Long.toString(accountRepository.listOneAdmin().getAccount_id());
            convert = "disagree";
        }

        acc = accountService.findUserById(Long.parseLong(user1Id));
        Movie_Schedule ms = (Movie_Schedule) session.getAttribute("ms");
        Movie_Room_Date mrd = (Movie_Room_Date) session.getAttribute("mrd");
        double sum = (double) session.getAttribute("sum");
        List<Seat> list = (List<Seat>) session.getAttribute("list");
        redirectAttributes.addFlashAttribute("back", "/");
        for (Seat s : list) {
            List<Ticket> tickets = ticketRepository.findByScheduleANDSeatNotCanceled(ms.getMovie_schedule_id(),
                    s.getId());
            if (tickets.size() != 0) {
                redirectAttributes.addFlashAttribute("error", "Seat was booked");
                return "redirect:/confirmByUser";
            }
        }
        if (infor.equals("ok") && convert == null) {
            redirectAttributes.addFlashAttribute("infor", infor);
            redirectAttributes.addFlashAttribute("user", acc);
            redirectAttributes.addFlashAttribute("mess", "Please choose type to convert!");
            redirectAttributes.addFlashAttribute("back",
                    "/seatchose/" + movie.getMovie_id() + "/" + ms.getMovie_schedule_id());
            return "redirect:/confirmByUser";
        }
        redirectAttributes.addFlashAttribute("scoreConvert", 0);

        double curSum = 0;
        String code = generateRandomString();
        if (promotion.equals("0")) {
            invoice.setPromotion(null);
            curSum = sum;
        } else {
            Promotion p = promotionRepository.findById(Long.parseLong(promotion)).get();
            invoice.setPromotion(p);
            curSum = sum * (100 - p.getDiscountPercentage()) / 100;
        }
        invoice.setUser_id(acc.getAccount_id());
        invoice.setTicketList(null);
        if ((acc.getRoleEntity().getName().equals("USER")) && infor.isEmpty()) {
            invoice.setStatus(InvoiceStatus.UNPAID);
        }

        if (infor.isEmpty() && (acc.getRoleEntity().getName().equals("USER"))) {
            invoice.setCode(code);
        } else {
            invoice.setCode(null);
            invoice.setStatus(InvoiceStatus.PAID);
        }
        if ((acc.getRoleEntity().getName().equals("USER")) && infor.equals("ok")) {
            if (promotion.equals("0")) {
                if (convert.equals("agree")) {
                    double r = acc.getScore() * 1000 - sum;
                    if (r < 0) {
                        redirectAttributes.addFlashAttribute("inforConvert",
                                "Member score is not enough to convert ticket");
                        return "redirect:/confirmByUser";
                    } else {
                        curSum = sum;
                        redirectAttributes.addFlashAttribute("scoreConvert", sum / 1000);
                        acc.setScore(acc.getScore() - (sum / 1000));
                        invoice.setUsePoint(true);
                    }
                } else {
                    acc.setScore(acc.getScore() + (sum / 1000 * 0.1));
                    invoice.setUsePoint(false);
                }
            }
        }

        if (!acc.getRoleEntity().getName().equals("USER")) {
            invoice.setUsePoint(false);
        }
        accountRepository.save(acc);
        invoice.setPrice(curSum);
        if (invoice.getUsePoint() == null) {
            invoice.setUsePoint(false);
        }
        invoice = invoiceService.addInvoice(invoice);

        for (Seat s : list) {
            Ticket ticket = new Ticket();
            ticket.setInvoice(invoice);
            ticket.setMovie_schedule(ms);
            ticket.setSeat(s);
            if ((acc.getRoleEntity().getName().equals("USER")) && infor.isEmpty()) {
                ticket.setStatus(TicketStatus.PENDING);
            } else {
                ticket.setStatus(TicketStatus.PAID);
            }
            ticketRepository.save(ticket);
        }

        if (infor.isEmpty() && (acc.getRoleEntity().getName().equals("USER"))) {
            Invoice i = invoiceRepository.findInvoiceByCode(code);
            invoice.setUsePoint(false);
            invoice.setQr(i.getInvoice_id() + "-QRCODE.png");
            invoiceRepository.save(invoice);
            String path = "http://localhost:8080/invoice/confirm/" + i.getInvoice_id();
            String rootQr = System.getProperty("user.dir") + File.separator + "src\\main\\resources\\static\\qr"
                    + File.separator + i.getInvoice_id() + "-QRCODE.png";
            qrCodeGenerate.generate(i.getInvoice_id(), path);
            accountService.sendQR(acc.getEmail(), rootQr, i.getCode(), movie.getMovie_title_en(), mrd.getMovie_date(),
                    mdf.movieTime_24H(ms.getSchedule().getValue()));
            redirectAttributes.addFlashAttribute("mess",
                    "We have sent you QR CODE to your email. Please pick up the ticket 30 minutes before the show.");
        } else {
            redirectAttributes.addFlashAttribute("mess",
                    "Booking successfully!");
        }
        redirectAttributes.addFlashAttribute("infor", infor);
        redirectAttributes.addFlashAttribute("user", acc);
        return "redirect:/confirmByUser";
    }

    @PostMapping("/checkUser")
    public String checkUser(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String email = request.getParameter("email");
        String movieId = request.getParameter("movieId");
        String msID = request.getParameter("msId");
        Optional<AccountEntity> acc = accountRepository.findUserByEmail(email);
        if (acc.isPresent()) {
            AccountEntity a = acc.get();
            redirectAttributes.addFlashAttribute("infor", "ok");
            redirectAttributes.addFlashAttribute("userId", a.getAccount_id());
            redirectAttributes.addFlashAttribute("user", a);
            redirectAttributes.addFlashAttribute("email", a.getEmail());
        } else {
            redirectAttributes.addFlashAttribute("infor", "fail");
        }
        redirectAttributes.addFlashAttribute("back", "/seatchose/" + movieId + "/" + msID);
        return "redirect:/confirmByUser";
    }

    // user controller to get list of tickets
    @GetMapping("/tickets/list")
    public String showListTicket(Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false) TicketStatus status,
            HttpServletRequest request) {
        if (account_util.isAuthenticated(authentication)) {
            status = TicketStatus.PENDING;
            return getListTicket(model, authentication, status, true, 0, 5, request, redirectAttributes);
        } else {
            redirectAttributes.addFlashAttribute("error", "You must login to view this.");
            return "redirect:/login";
        }
    }

    @GetMapping("/tickets/list/{offset}/{pageSize}")
    public String getListTicket(Model model, Authentication authentication,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) boolean byManagers,
            @PathVariable("offset") int offset,
            @PathVariable("pageSize") int pageSize,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (account_util.isAuthenticated(authentication)) {
            AccountEntity user = accountService
                    .findUserByUserName(account_util.myUserDetails(authentication).getUsername());

            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);

            Page<Ticket> tickets;
            if (isManager && byManagers) {
                // user is manager and byManagers flag turned on: get all tickets made by
                // managers
                tickets = ticketService.findTicketCreatedByManagerWithPaging(offset, pageSize, status);
            } else if (isManager) {
                // If the user is a manager, get all tickets
                tickets = ticketService.findTicketWithPaging(offset, pageSize, status);
            } else {
                // If the user is not a manager, get only their tickets
                tickets = ticketService.findTicketByUserIdWithPaging(user.getAccount_id(), offset, pageSize, status);
            }
            List<TicketDisplayAdminDto> ticketDisplay = ticketService.convertTicketsToDisplay(tickets, authentication);

            long totalItems = tickets.getTotalElements();
            int totalPage = tickets.getTotalPages();

            model.addAttribute("tickets", ticketDisplay);
            model.addAttribute("user", user);
            model.addAttribute("isManager", isManager);
            model.addAttribute("offset", offset); // page num, deffault 0
            model.addAttribute("pageSize", pageSize); // item per page
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPage", totalPage);
            model.addAttribute("ticketStatus", status);
            model.addAttribute("byManagers", byManagers);
            return "ticket_list"; // return the name of the view

        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";
    }

    @GetMapping("ticket/{ticketId}")
    public String getTicketById(Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @PathVariable("ticketId") Long ticketId) {
        if (account_util.isAuthenticated(authentication)) {
            AccountEntity user = accountService
                    .findUserByUserName(account_util.myUserDetails(authentication).getUsername());

            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            Ticket ticket = new Ticket();
            if (isManager) {
                ticket = ticketService.getTicketByID(ticketId);
            } else {
                ticket = ticketService.getTicketByIDAndUserID(ticketId, user.getAccount_id());
            }
            if (ticket == null) {
                redirectAttributes.addFlashAttribute("error", "Ticket is missing.");

                return "redirect:/tickets/list";
            }
            model.addAttribute("ticket", ticket);
            model.addAttribute("user", user);
            model.addAttribute("isManager", isManager);
            return "ticket_info"; // ticket info

        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";
    }

    @PostMapping("ticket/edit/{ticketId}")
    public String editTicketStatus(Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @PathVariable("ticketId") Long ticketId,
            @RequestParam(required = false) TicketStatus status) {
        if (account_util.isAuthenticated(authentication)) {
            AccountEntity user = accountService
                    .findUserByUserName(account_util.myUserDetails(authentication).getUsername());

            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            Ticket ticket = new Ticket();
            if (isManager) {
                ticket = ticketService.getTicketByID(ticketId);

            } else {
                ticket = ticketService.getTicketByIDAndUserID(ticketId, user.getAccount_id());
            }
            if (ticket == null) {
                redirectAttributes.addFlashAttribute("error", "Ticket is missing.");

                return "redirect:/tickets/list";
            }
            Ticket newTicket = ticketService.updateTicketStatus(ticket, status, isManager);
            model.addAttribute("ticket", newTicket);
            model.addAttribute("user", user);
            model.addAttribute("isManager", isManager);
            return "ticket_info"; // ticket info
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/home";
    }

    // QuangVD14
    @GetMapping("/user_booking/{chose}")
    public String showBookedTicket(
            @PathVariable("chose") int chose, Model theModel, Authentication authentication) {
        // if (authentication != null && authentication.isAuthenticated()) {
        // List<TicketResponseDTO> list = new ArrayList<>();
        // MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
        // Long user_id = userDetails.getId();
        // if (chose == 1) {
        // list = ticketService.findAllTicketByUserIdAndStatusBooking(user_id);
        // } else if (chose == 2) {
        // list = ticketService.findAllTicketByUserIdAndStatusBooked(user_id);
        // } else if (chose == 3) {
        // list = ticketService.findAllTicketByUserIdAndStatusCancel(user_id);
        // } else if (chose == 4) {
        // list = ticketService.findAllTicketByUserId(user_id);
        // }
        // theModel.addAttribute("tickets", list);
        // }
        return "redirect:/user_booking/" + chose + "/search/" + 1;
    }

    @GetMapping("/user_booking/{chose}/search/{currentPage}")
    public String userBookingSearch(Authentication authentication, Model model,
            @PathVariable("chose") int chose,
            @RequestParam(name = "user_name", required = false) String user_name,
            @RequestParam(name = "movieNameEn", required = false) String movie_name,
            @RequestParam(name = "movieDate", required = false) String date_raw,
            @RequestParam(name = "schedule", required = false) String schedule_raw,
            @RequestParam(name = "seatPosition", required = false) String seat_position,
            @PathVariable("currentPage") int currentPage) {
        int pageable = currentPage;
        Movie_Duration_Formatter mrd = new Movie_Duration_Formatter();
        List<TicketResponseDTO> list = new ArrayList<>();
        List<Ticket> tickets = new ArrayList<>();
        Page<Ticket> t;
        if (authentication != null && authentication.isAuthenticated()) {
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
            AccountEntity accountEntity = accountService.findUserByUserName(userDetails.getUsername());
            // Handle the date
            java.sql.Date date;
            if (date_raw != null && !date_raw.isEmpty()) {
                date = java.sql.Date.valueOf(date_raw);
            } else {
                date = null;
            }
            // Handle the schedule
            int schedule = 0;
            if (schedule_raw != null && !schedule_raw.isEmpty()) {
                Movie_Duration_Formatter m = new Movie_Duration_Formatter();
                schedule = m.getMinutesFromString(schedule_raw);
            }
            // Get the list
            if (accountEntity != null) {
                if (chose == 1) {
                    t = ticketService.searchTicketBooking(accountEntity.getAccount_id(), movie_name, date,
                            schedule, seat_position, pageable);
                    tickets = t.getContent();

                } else if (chose == 2) {
                    t = ticketService.searchTicketBooked(accountEntity.getAccount_id(), movie_name, date,
                            schedule, seat_position, pageable);
                    tickets = t.getContent();

                } else if (chose == 3) {
                    t = ticketService.searchTicketCancel(accountEntity.getAccount_id(), movie_name,
                            date, schedule, seat_position, pageable);
                    tickets = t.getContent();
                } else {
                    t = ticketService.searchAllTicketOfUser(accountEntity.getAccount_id(), movie_name,
                            date, schedule, seat_position, pageable);
                    tickets = t.getContent();
                }
                model.addAttribute("totalPage", t.getTotalPages());

            }

            for (int i = 0; i < tickets.size(); i++) {
                TicketResponseDTO ticketResponseDTO = new TicketResponseDTO();
                ticketResponseDTO.setUsername(
                        accountService.findUserById(tickets.get(i).getInvoice().getUser_id()).getUsername());
                ticketResponseDTO.setMovie_date(tickets.get(i).getMovie_schedule().getMrd_item().getMovie_date());
                ticketResponseDTO
                        .setValue(mrd.movieTime_24H(tickets.get(i).getMovie_schedule().getSchedule().getValue()));
                ticketResponseDTO.setMovie_title_en(
                        tickets.get(i).getMovie_schedule().getMrd_item().getMovie().getMovie_title_en());
                ticketResponseDTO.setPosition(tickets.get(i).getSeat().getPosition());
                ticketResponseDTO.setPrice(tickets.get(i).getSeat().getType().getPrice());
                ticketResponseDTO.setId(tickets.get(i).getTicket_id());
                if (tickets.get(i).getStatus().name().equals("PENDING")) {
                    ticketResponseDTO.setStatus("Booking");
                } else if (tickets.get(i).getStatus().name().equals("DROPPED")) {
                    ticketResponseDTO.setStatus("Dropped");
                } else if (tickets.get(i).getStatus().name().equals("CANCEL")) {
                    ticketResponseDTO.setStatus("Cancel");
                } else if (tickets.get(i).getStatus().name().equals("PAID")) {
                    ticketResponseDTO.setStatus("Booked");
                }
                list.add(ticketResponseDTO);
            }
            // Ticket t = ticketService.changeTicketStatus(tickets);
            model.addAttribute("chose", chose);
            model.addAttribute("user_name", user_name);
            model.addAttribute("movieNameEn", movie_name);
            model.addAttribute("movieDate", date);
            model.addAttribute("schedule", schedule_raw);
            model.addAttribute("seatPosition", seat_position);
            model.addAttribute("tickets", list);
            model.addAttribute("currentPage", currentPage);
        }
        return "user_booking";
    }

    @PostMapping("/user_booking/{chose}/change_status/{id}/{currentPage}")
    public String changeStatusTicket(@PathVariable("id") Long id,
            @PathVariable("chose") int chose,
            @PathVariable("currentPage") int currentPage) {
        Ticket ticket = ticketService.getTicketByID(id);
        if (ticket.getStatus() == TicketStatus.PENDING) {
            Ticket t = ticketService.changeTicketStatus(ticketService.getTicketByID(id), TicketStatus.CANCEL);
        }
        return "redirect:/user_booking/" + chose + "/search/" + currentPage;
    }
    // QuangVD14

    // ToanHK6
    @GetMapping("ticket/manage")
    public String showListTicketManager(Authentication authentication, Model model) {
        String username = null;
        String movieNameEn = null;
        String movieDate = null;
        String schedule = null;
        String roomName = null;
        return showAllByPage(authentication, 1, username, movieNameEn, movieDate, schedule, roomName, model);
    }

    @GetMapping("ticket/manage/page/{pageNumber}")
    public String showAllByPage(Authentication authentication,
            @PathVariable("pageNumber") int currentPage,
            @Param("username") String username,
            @Param("movieNameEn") String movieNameEn,
            @Param("movieDate") String movieDate,
            @Param("schedule") String schedule,
            @Param("roomName") String roomName,
            Model model) {
        Movie_Duration_Formatter mrd = new Movie_Duration_Formatter();
        AccountEntity a = accountService.findUserByUserName(username);
        java.sql.Date date;

        if (movieDate != null && !movieDate.isEmpty()) {
            date = java.sql.Date.valueOf(movieDate);
        } else {
            date = null;
        }
        // Handle schedule
        int rawSchedule = 0;
        if (schedule != null && !schedule.isEmpty()) {
            Movie_Duration_Formatter m = new Movie_Duration_Formatter();
            rawSchedule = m.getMinutesFromString(schedule);
        }

        // Handle page 2.0
        Page<Movie_Schedule> m;
        if (a == null) {
            m = movie_scheduleService.searchMovieSchedule(currentPage, rawSchedule, roomName, date, movieNameEn);
        } else {
            m = movie_scheduleService.findScheduleWithAdminTicket(currentPage, rawSchedule, roomName, date, movieNameEn,
                    a.getAccount_id());
        }

        // Handle dto 2.0
        List<Movie_Schedule> movie_schedules = m.getContent();
        List<Movie_ScedulesDto> movie_scedulesDtos = new ArrayList<>();
        for (int i = 0; i < movie_schedules.size(); i++) {
            Movie_ScedulesDto ms = new Movie_ScedulesDto();
            ms.setMovie_schedule_id(movie_schedules.get(i).getMovie_schedule_id());
            ms.setRoom_name(movie_schedules.get(i).getMrd_item().getRoom().getRoom_name());
            ms.setMovie_name(movie_schedules.get(i).getMrd_item().getMovie().getMovie_title_en());
            ms.setMovie_date(movie_schedules.get(i).getMrd_item().getMovie_date());
            ms.setSchedule(mrd.movieTime_24H(movie_schedules.get(i).getSchedule().getValue()));
            movie_scedulesDtos.add(ms);
        }
        long totalItems = m.getTotalElements();
        int totalPage = m.getTotalPages();
        MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
        model.addAttribute("movie_schedules", movie_scedulesDtos);
        model.addAttribute("oldName", userDetails.getUsername());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("username", username);
        model.addAttribute("movieNameEn", movieNameEn);
        model.addAttribute("movieDate", date);
        model.addAttribute("schedule", schedule);
        model.addAttribute("room_name", roomName);
        model.addAttribute("isManager", account_util.isUserManager(authentication));
        return "booking";
    }

    @GetMapping("/ticket")
    public String showListTicketUser() {
        return "user_booking";
    }

    @GetMapping("/ticket/invoice")
    public String showInvoiceList() {
        return "invoice";
    }

    @GetMapping("/ticket/ms_id/{movie_schedule_id}")
    public String updateTicketStatus(@PathVariable("movie_schedule_id") long id,
            Model model) {
        Movie_Seat_Formatter msf = new Movie_Seat_Formatter();
        Cinema_Room cinemaRoom = movie_scheduleService.getMovieSchedulesByID(id).getMrd_item().getRoom();
        List<Seat> seatRoom = msf.sortSeatsInARoom(cinemaRoom.getSeat_row(), cinemaRoom.getSeat_column(),
                cinemaRoom.getSeatList());
        List<TicketSeatDto> ticketSeatDtos = new ArrayList<>();
        for (int i = 0; i < seatRoom.size(); i++) {
            TicketSeatDto ticketSeatDto = new TicketSeatDto();
            List<Ticket> t = ticketService.findByScheduleANDSeatNotCanceled(id, seatRoom.get(i).getId());
            if (t.size() > 0) {
                Ticket temp = t.get(0);
                ticketSeatDto.setTicket_id(temp.getTicket_id());
                ticketSeatDto.setTicketStatus(temp.getStatus().toString());
                ticketSeatDto.setUsermail(accountService.findUserById(temp.getInvoice().getUser_id()).getEmail());
                ticketSeatDto.setUsername(accountService.findUserById(temp.getInvoice().getUser_id()).getUsername());
            } else {
                ticketSeatDto.setTicket_id(-1);
                ticketSeatDto.setTicketStatus(null);
            }
            ticketSeatDto.setSeat_position(seatRoom.get(i).getPosition());
            ticketSeatDto.setSeat_id(seatRoom.get(i).getId());

            ticketSeatDtos.add(ticketSeatDto);
        }
        Seat_to_2D st2 = new Seat_to_2D();
        List<List<TicketSeatDto>> ticketSeatDtos2D = st2.convertTo2D_Ticket_Seat_Dto(ticketSeatDtos,
                cinemaRoom.getSeat_row(), cinemaRoom.getSeat_column());
        model.addAttribute("movie_schedule_id", id);
        model.addAttribute("seats", ticketSeatDtos2D);
        return "update_ticket";
    }

    @PostMapping("/ticket/ms_id/update/{movie_schedule_id}")
    public String updateTicketStatusPaid(@PathVariable("movie_schedule_id") long id,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model,
            Authentication authentication) {
        boolean isManager = account_util.isUserManager(authentication);
        String act = request.getParameter("act");
        String[] idTicket = request.getParameterValues("seat");
        List<String> ticketId_raw = Arrays.asList(idTicket);
        for (int i = 0; i < ticketId_raw.size(); i++) {
            Ticket ticket = ticketService.getTicketByID(Long.parseLong(ticketId_raw.get(i)));
            if (act.equals("Set Pending")) {
                ticketService.updateTicketStatus(ticket, TicketStatus.PENDING, isManager);
            } else if (act.equals("Set Paid")) {
                ticketService.updateTicketStatus(ticket, TicketStatus.PAID, isManager);
            } else if (act.equals("Set Canceled")) {
                ticketService.updateTicketStatus(ticket, TicketStatus.CANCEL, isManager);
            } else if (act.equals("Set Dropped")) {
                ticketService.updateTicketStatus(ticket, TicketStatus.DROPPED, isManager);
            }
            ticketService.saveTicket(ticket);
        }
        return "redirect:/ticket/ms_id/" + id;
    }

    @GetMapping("/bookingTicket")
    public String getBookingTicket(Model model, RedirectAttributes redirectAttributes) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        List<Movie_Room_Date> mrd = mDateRepository.findAllMovieToday();
        List<Movie> movies = movieRepository.getMoviesHaveSchedules();
        model.addAttribute("movies", movies);
        if (mrd.isEmpty()) {
            redirectAttributes.addFlashAttribute("mess", "The date does not have movies");
            return "redirect:/bookingTicket/" + formattedDate;
        }
        redirectAttributes.addFlashAttribute("mrd", mrd);
        return "redirect:/bookingTicket/" + formattedDate;
    }

    @GetMapping({ "/bookingTicket/{date}/{id}", "/bookingTicket/{date}" })
    public String getDetailDate(@PathVariable("date") String date, @PathVariable(required = false) Long id,
            RedirectAttributes redirectAttributes, Model model, HttpSession session) {
        List<Movie_Room_Date> mrd_origin = new ArrayList<>();
        List<Movie> movies = movieRepository.getMoviesHaveSchedules();
        String preDate = (String) session.getAttribute("preDate");
        Long preId = (Long) session.getAttribute("preId");
        if (preDate != null && preId != null && preDate.equals(date) && preId.equals(id)) {
            return "redirect:/bookingTicket/" + date;
        }
        model.addAttribute("movies", movies);
        session.setAttribute("preDate", date);
        session.setAttribute("preId", id);
        if (id == null) {
            mrd_origin = mDateRepository.findAllMovieByDateWithSchedules(java.sql.Date.valueOf(date));
        } else {
            mrd_origin = mDateRepository.findAllMovieByDateWithSchedulesAndMovie(java.sql.Date.valueOf(date), id);
        }
        if (mrd_origin.isEmpty()) {
            model.addAttribute("mess", "The date does not have movies");
            return "bookingTicket";
        }
        Map<Long, Integer> ticketCounts = new HashMap<>();
        for (Movie_Room_Date mrd : mrd_origin) {
            for (Movie_Schedule time : mrd.getSchedules()) {
                long movieScheduleId = time.getMovie_schedule_id();
                int count = ticketRepository.findBySchedule(movieScheduleId).size();
                ticketCounts.put(movieScheduleId, count);
            }
        }

        model.addAttribute("mrd", mrd_origin);
        model.addAttribute("ticketCounts", ticketCounts);
        return "bookingTicket";
    }
}
