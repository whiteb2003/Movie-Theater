package com.group5.cpl.controller;

import com.group5.cpl.model.*;
import com.group5.cpl.model.enums.Movie_status;
import com.group5.cpl.repository.GenreRepository;
import com.group5.cpl.repository.MovieRepository;
import com.group5.cpl.repository.Movie_Room_DateRepository;
import com.group5.cpl.repository.Movie_ScheduleRepository;
import com.group5.cpl.repository.RoomRepository;
import com.group5.cpl.repository.ScheduleRepository;
import com.group5.cpl.repository.TicketRepository;
import com.group5.cpl.service.*;

import com.group5.cpl.utils.Account_Util;
import com.group5.cpl.utils.Movie_Duration_Formatter;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    MovieService movieService;
    @Autowired
    GenreService genreService;
    @Autowired
    Account_Util account_util;
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    Movie_Room_DateService movie_Room_DateService;
    @Autowired
    Movie_ScheduleRepository movie_ScheduleRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    Movie_Room_DateRepository movie_Room_DateRepository;

    @GetMapping({ "/manage", "manage/active" })
    public String showMovieActive(Model model, Authentication authentication) {
        String keyword = null;
        return listByPageManageActive(1, keyword, model, authentication);
    }

    @GetMapping("/manage/hidden")
    public String showMovieHidden(Model model, Authentication authentication) {
        String keyword = null;
        return listByPagemovieManageHidden(1, keyword, model, authentication);
    }

    @GetMapping({ "/manage/page/{pageNumber}", "manage/active/page/{pageNumber}" })
    public String listByPageManageActive(@PathVariable("pageNumber") int currentPage, @Param("keyword") String keyword,
            Model model, Authentication authentication) {
        Page<Movie> page = movieService.getActiveMovies(currentPage, keyword);
        long totalItems = page.getTotalElements();
        int totalPage = page.getTotalPages();
        List<Movie> list = page.getContent();
        boolean isManager = account_util.isUserManager(authentication);
        model.addAttribute("isManager", isManager);
        model.addAttribute("movies", list);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("key", "active");
        return "movie_manage";
    }

    @GetMapping("/manage/hidden/page/{pageNumber}")
    public String listByPagemovieManageHidden(@PathVariable("pageNumber") int currentPage,
            @Param("keyword") String keyword, Model model, Authentication authentication) {
        Page<Movie> page = movieService.getHiddenMovies(currentPage, keyword);
        long totalItems = page.getTotalElements();
        int totalPage = page.getTotalPages();
        List<Movie> list = page.getContent();
        boolean isManager = account_util.isUserManager(authentication);
        model.addAttribute("isManager", isManager);
        model.addAttribute("movies", list);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("key", "hidden");
        return "movie_manage";
    }

    @GetMapping("/add_movie")
    public String addMovieView(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("listGenre", genreRepository.findAll());
        return "add_movie";
    }

    @PostMapping("/save_movie")
    public String addMovie(@ModelAttribute("movie") Movie movie,
            @RequestParam("imagePoster") MultipartFile multipartFile,
            @RequestParam("imageBanner") MultipartFile extraMultipartFile, HttpServletRequest request, Model model,
            RedirectAttributes redirectAttributes) throws ParseException {
        String movieId = request.getParameter("movieId");
        String status = request.getParameter("status");
        String filePosterName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        String fileBannerName = StringUtils.cleanPath(extraMultipartFile.getOriginalFilename());
        String poster = request.getParameter("poster");
        String banner = request.getParameter("banner");
        this.storageService.store(multipartFile);
        this.storageService.store(extraMultipartFile);

        if (filePosterName.isEmpty()) {
            filePosterName = poster;
        }
        if (fileBannerName.isEmpty()) {
            fileBannerName = banner;
        }
        model.addAttribute("poster", filePosterName);
        model.addAttribute("banner", fileBannerName);
        model.addAttribute("PosterImagePath", "/images/" + filePosterName);
        model.addAttribute("BannerImagePath", "/images/" + fileBannerName);
        if (movieId.isEmpty()) {
            Movie existMovieENG = movieService.findByENGName(movie.getMovie_title_en().replaceAll("\\s+", " ").trim());
            model.addAttribute("listGenre", genreRepository.findAll());
            if (existMovieENG != null) {
                model.addAttribute("errorENG", "The name movie English was exist");
                return "add_movie";
            }

            Movie existMovieVIE = movieService.findByVIEName(movie.getMovie_title_vn().replaceAll("\\s+", " ").trim());
            if (existMovieVIE != null) {
                model.addAttribute("errorVIE", "The name movie Vietnamese was exist");
                return "add_movie";
            }
        } else {
            Movie moviee = movieService.findById(Long.parseLong(movieId));
            model.addAttribute("movie", moviee);
            model.addAttribute("movieId", movieId);
            model.addAttribute("status", moviee.getMovie_status().toString());
            Movie existMovieENG = movieRepository
                    .findAllByNameENGAndId(movie.getMovie_title_en().replaceAll("\\s+", " ").trim(), movieId);
            model.addAttribute("listGenre", genreRepository.findAll());
            if (existMovieENG != null) {
                model.addAttribute("errorENG", "The name movie English was exist");
                return "add_movie";
            }

            Movie existMovieVIE = movieRepository
                    .findAllByNameVIEAndId(movie.getMovie_title_vn().replaceAll("\\s+", " ").trim(), movieId);
            if (existMovieVIE != null) {
                model.addAttribute("errorVIE", "The name movie Vietnamese was exist");
                return "add_movie";
            }

            if (status.equals("HIDDEN")) {
                List<Movie_Room_Date> mrd = movie_Room_DateRepository.getByMovie(Long.parseLong(movieId));
                for (Movie_Room_Date m : mrd) {
                    if (m.getMovie_date().toLocalDate().compareTo(LocalDate.now()) >= 0) {
                        List<Movie_Schedule> ms = movie_ScheduleRepository.getByMrdId(m.getMrd_id());
                        for (Movie_Schedule mss : ms) {
                            List<Ticket> tickets = ticketRepository.findBySchedule(mss.getMovie_schedule_id());
                            if (!tickets.isEmpty()) {
                                model.addAttribute("error",
                                        "The movie has been booked. You can not hidden");
                                return "add_movie";
                            }
                        }
                    }
                }
            }
        }

        movie.setMovie_status(Movie_status.ACTIVE);
        movie.setPoster(filePosterName);
        movie.setBanner(fileBannerName);
        if (status.equals("HIDDEN")) {
            movie.setMovie_status(Movie_status.HIDDEN);
        }

        List<String> valueGenres = Arrays.asList(request.getParameterValues("genre"));
        List<Genre> genres = new ArrayList<>();
        for (String gName : valueGenres) {
            genres.add(genreService.getGenreByName(gName));
        }
        movie.setMovie_genres(genres);
        movieService.addMovie(movie);

        redirectAttributes.addFlashAttribute("success", "Save successfully!!!");
        if (!movieId.isEmpty()) {
            Movie movie2 = movieRepository.getMovieById(Long.parseLong(movieId));
            if (movie2.getMovie_status().equals(Movie_status.ACTIVE)) {
                return "redirect:/movies/schedule/" + movieId;
            }
            return "redirect:/movies/manage";

        } else {
            if (movieRepository.getTop().getMovie_status().equals(Movie_status.ACTIVE)) {
                return "redirect:/movies/schedule/" + movieRepository.getTop().getMovie_id();
            }
            return "redirect:/movies/manage";
        }
    }

    @GetMapping("/edit/{id}")
    public String editMovie(@PathVariable("id") Long id, Model model) {
        Movie movie = movieService.findById(id);
        model.addAttribute("PosterImagePath", movie.getPosterImagePath());
        model.addAttribute("BannerImagePath", movie.getBannerImagePath());
        model.addAttribute("poster", movie.getPoster());
        model.addAttribute("banner", movie.getBanner());
        model.addAttribute("movie", movie);
        model.addAttribute("movieId", id);
        model.addAttribute("listGenre", genreRepository.findAll());
        model.addAttribute("status", movie.getMovie_status().toString());
        return "add_movie";
    }

    @GetMapping("/schedule/{id}")
    public String showSchedule(@PathVariable("id") Long id, Model model, @ModelAttribute("success") String success) {
        Movie movie = movieService.findById(id);
        List<Movie_Room_Date> mrd = movie_Room_DateRepository.findByMovieId(id);
        List<String> mrd_date = movie_Room_DateRepository.getMoviesById(id);
        List<Movie_Room_Date> mrd_Room_DateDuplicate = new ArrayList<>();
        List<Date> date = new ArrayList<>();
        List<Date> dateExist = new ArrayList<>();
        List<Cinema_Room> roomHasMovie = new ArrayList<>();
        List<Schedule> scheduleExist = new ArrayList<>();
        for (String s : mrd_date) {
            date.add(java.sql.Date.valueOf(s));
        }
        List<Movie_Schedule> selectedSchedules = new ArrayList<>();
        if (mrd != null) {
            for (Movie_Room_Date mrdValue : mrd) {
                List<Movie_Schedule> schedules = movie_ScheduleRepository.getByMrdId(mrdValue.getMrd_id());
                selectedSchedules.addAll(schedules);
            }
            for (Movie_Room_Date mv : mrd) {
                boolean check = false;
                List<Movie_Schedule> movie_Schedules = movie_ScheduleRepository.getByMrdId(mv.getMrd_id());
                for (Movie_Schedule ms : movie_Schedules) {
                    List<Ticket> tickets = ticketRepository.findBySchedule(ms.getMovie_schedule_id());
                    if (!tickets.isEmpty()) {
                        check = true;
                        break;
                    }
                }
                if (check) {
                    mrd_Room_DateDuplicate.add(mv);
                    roomHasMovie.add(mv.getRoom());
                }
            }

            for (Movie_Room_Date m : mrd_Room_DateDuplicate) {
                List<Movie_Schedule> movie_Schedules = movie_ScheduleRepository.getByMrdId(m.getMrd_id());
                for (Movie_Schedule ms : movie_Schedules) {
                    List<Ticket> tickets = ticketRepository.findBySchedule(ms.getMovie_schedule_id());
                    if (!tickets.isEmpty()) {
                        dateExist.add(m.getMovie_date());
                        scheduleExist.add(ms.getSchedule());
                    }
                }
            }
        }
        Collections.sort(mrd, new Comparator<Movie_Room_Date>() {
            @Override
            public int compare(Movie_Room_Date mrd1, Movie_Room_Date mrd2) {
                return mrd1.getMovie_date().compareTo(mrd2.getMovie_date());
            }
        });
        Collections.sort(date);
        List<Schedule> schedules = scheduleRepository.findAll();
        Collections.sort(schedules, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule s1, Schedule s2) {
                return s1.getValue().compareTo(s2.getValue());
            }
        });

        model.addAttribute("movie", movie);
        model.addAttribute("movieId", id);
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("schedules", schedules);
        model.addAttribute("mrd", mrd);
        model.addAttribute("selectedSchedules", selectedSchedules);
        model.addAttribute("success", success);
        model.addAttribute("date", date);
        model.addAttribute("roomHasMovie", roomHasMovie);
        model.addAttribute("dateExist", dateExist);
        model.addAttribute("scheduleExist", scheduleExist);
        return "add_schedule";
    }

    @PostMapping("/save_schedule")
    public String addSchedule(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String movieId = request.getParameter("movieId");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        List<Movie_Room_Date> mrd_old = movie_Room_DateService.getByMovieId(Long.parseLong(movieId));

        List<String> roomValue = Arrays.asList(request.getParameterValues("room_name"));
        Movie movieNow = movieService.findById(Long.parseLong(movieId));
        List<String> movieDate = Arrays.asList(request.getParameterValues("movieDate"));
        List<String> valueSchedule = Arrays.asList(request.getParameterValues("schedule"));
        for (String room : roomValue) {
            List<Movie_Room_Date> mrd_exist_Room_Date = movie_Room_DateService.getByRoomAndMovie(room, movieId);
            List<Movie_Room_Date> mrd_ms = new ArrayList<>();
            if (mrd_exist_Room_Date.size() != 0) {
                for (Movie_Room_Date m : mrd_exist_Room_Date) {
                    for (String mDate : movieDate) {
                        if (m.getMovie_date().compareTo(java.sql.Date.valueOf(mDate)) == 0) {
                            mrd_ms.add(m);
                        }
                    }
                }

                for (Movie_Room_Date m : mrd_ms) {
                    List<Movie_Schedule> ms = movie_ScheduleRepository.getByMrdId(m.getMrd_id());
                    for (Movie_Schedule msc : ms) {
                        if (valueSchedule.contains(msc.getSchedule().getValue().toString())) {
                            redirectAttributes.addFlashAttribute("dateError",
                                    "Movie " + m.getMovie().getMovie_title_en() +
                                            ".In " + room + " and in date " + formatter.format(m.getMovie_date()) +
                                            " has same the movie date at " + (msc.getSchedule().getValue() / 60)
                                            + " : "
                                            + (msc.getSchedule().getValue() % 60 == 0 ? "00"
                                                    : msc.getSchedule().getValue() % 60));
                            return "redirect:/movies/schedule/" + movieId;
                        }
                    }
                }
            }
        }

        List<Movie_Room_Date> mrd_Room_DateDuplicate = new ArrayList<>();
        if (mrd_old.size() != 0) {
            List<Movie_Room_Date> mrd_Room_Date = new ArrayList<>();
            for (Movie_Room_Date mv : mrd_old) {
                boolean check = false;
                List<Movie_Schedule> movie_Schedules = movie_ScheduleRepository.getByMrdId(mv.getMrd_id());
                for (Movie_Schedule ms : movie_Schedules) {
                    List<Ticket> tickets = ticketRepository.findBySchedule(ms.getMovie_schedule_id());
                    if (!tickets.isEmpty()) {
                        check = true;
                        break;
                    }
                }
                if (check) {
                    mrd_Room_DateDuplicate.add(mv);
                } else {
                    mrd_Room_Date.add(mv);
                }
            }
            for (Movie_Room_Date mValue : mrd_Room_Date) {
                movie_ScheduleRepository.deleteByMrdId(mValue.getMrd_id());
                movie_Room_DateService.delete(mValue.getMrd_id());
            }
            for (Movie_Room_Date mValue : mrd_Room_DateDuplicate) {
                List<Movie_Schedule> movie_Schedule = movie_ScheduleRepository.getByMrdId(mValue.getMrd_id());
                for (Movie_Schedule ms : movie_Schedule) {
                    List<Ticket> existLicket = ticketRepository.findBySchedule(ms.getMovie_schedule_id());
                    if (existLicket.isEmpty()) {
                        movie_ScheduleRepository.deleteById(ms.getMovie_schedule_id());
                    }
                }
            }
        }

        for (String mDate : movieDate) {
            List<Movie_Schedule> exist = new ArrayList<>();
            if (movieNow.getRelease_date().compareTo(java.sql.Date.valueOf(mDate)) > 0) {
                redirectAttributes.addFlashAttribute("dateError", "The show date must larger than the release date");
                return "redirect:/movies/schedule/" + movieId;
            }
            if (!mrd_Room_DateDuplicate.isEmpty()) {
                for (Movie_Room_Date mRoom_Date : mrd_Room_DateDuplicate) {
                    if (mRoom_Date.getMovie_date().compareTo(java.sql.Date.valueOf(mDate)) == 0) {
                        exist = movie_ScheduleRepository.getByMrdId(mRoom_Date.getMrd_id());
                        break;
                    }
                }
            }

            if (!exist.isEmpty()) {
                for (String vName : valueSchedule) {
                    for (Movie_Schedule movie_Schedule : exist) {
                        Movie_Schedule ms = new Movie_Schedule();
                        if (movie_Schedule.getSchedule().getValue() != Integer.parseInt(vName)) {
                            ms.setSchedule(scheduleRepository.findByValue(vName));
                            ms.setMrd_item(movie_Schedule.getMrd_item());
                            movie_ScheduleRepository.save(ms);
                        }
                    }
                }
            }
            for (String room : roomValue) {
                if (movie_Room_DateRepository
                        .getMoviesByAndRoomAndDate(movieNow.getMovie_id(), room, java.sql.Date.valueOf(mDate))
                        .size() != 1) {
                    Movie_Room_Date mrd = new Movie_Room_Date();
                    mrd.setMovie(movieNow);
                    mrd.setMovie_date(java.sql.Date.valueOf(mDate));
                    mrd.setRoom(roomRepository.findByName(room));
                    movie_Room_DateService.save(mrd);
                    for (String vName : valueSchedule) {
                        Movie_Schedule ms = new Movie_Schedule();
                        ms.setSchedule(scheduleRepository.findByValue(vName));
                        ms.setMrd_item(mrd);
                        movie_ScheduleRepository.save(ms);
                    }
                }
            }
        }
        redirectAttributes.addFlashAttribute("success", "Save successfully!!!");

        return "redirect:/movies/schedule/" + movieId;
    }

    @GetMapping("/hidden/{id}")
    public String hiddenMovie(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        List<Movie_Room_Date> mrd = movie_Room_DateRepository.getByMovie(id);
        for (Movie_Room_Date m : mrd) {
            if (m.getMovie_date().toLocalDate().compareTo(LocalDate.now()) >= 0) {
                List<Movie_Schedule> ms = movie_ScheduleRepository.getByMrdId(m.getMrd_id());
                for (Movie_Schedule mss : ms) {
                    List<Ticket> tickets = ticketRepository.findBySchedule(mss.getMovie_schedule_id());
                    if (!tickets.isEmpty()) {
                        redirectAttributes.addFlashAttribute("error", "The movie has been booked. You can not hidden");
                        return "redirect:/movies/manage";
                    }
                }
            }
        }
        movieRepository.hiddenMovie(id);
        return "redirect:/movies/manage";
    }

    @GetMapping("/active/{id}")
    public String activeMovie(@PathVariable("id") Long id) {
        movieRepository.activeMovie(id);
        return "redirect:/movies/manage/hidden";
    }

    // incompleted?

    @RequestMapping("manage/add_schedule")
    public String addSchedule() {

        return "add_schedule";
    }

    @GetMapping("movie/1")
    public String displayMovie() {
        return "movie_detail";
    }

    @GetMapping("select_seat")
    public String selectSeat() {
        return "select_seat";
    }

    @GetMapping("/movie_details/{id}")
    public String movieDetail(@PathVariable("id") Long id, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
            String user = userDetails.getUsername();
            AccountEntity u = accountService.findUserByUserName(user);

            model.addAttribute("userDetails", u);
        }
        Movie movie = movieService.findById(id);
        // tách videoId từ url
        String url = movie.getTrailer();
        String trailer = "";
        if (url != null && !url.isEmpty()) {
            if (url.contains("youtu.be")) {
                // Nếu URL là dạng rút gọn
                String[] parts = url.split("/");
                trailer = parts[parts.length - 1];
            } else if (url.contains("watch?v=")) {
                // Nếu URL là dạng nguyên bản
                String[] parts = url.split("v=");
                trailer = parts[1];
            }
        } else {
            // Xử lý trường hợp không có URL
            trailer = "Trailer does not exist";
        }
        Movie_Duration_Formatter dur = new Movie_Duration_Formatter();
        String duration = dur.durationformat(Integer.parseInt(movie.getDuration()));
        List<String> genres = new ArrayList<>();
        for (Genre g : movie.getMovie_genres()) {
            genres.add(g.getGenreName());
        }
        model.addAttribute("duration", duration);
        model.addAttribute("genres", genres);
        model.addAttribute("movie", movie);
        model.addAttribute("trailer", trailer);
        return "movie_details";
    }
}
