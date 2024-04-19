package com.group5.cpl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.Movie;
import com.group5.cpl.model.MyUserDetail;
import com.group5.cpl.service.AccountService;
import com.group5.cpl.service.MovieService;

@Controller
public class HomeController {
    @Autowired
    AccountService accountService;
    @Autowired
    MovieService movieService;

    @GetMapping({ "/", "/home" })
    public String showHomePage(Model model, Authentication authentication) {
        String keyword = null;
        return listByPage(1, 1, keyword, authentication, model);
    }

    @GetMapping("/page/{pageNumber}/{pageNumberUP}")
    public String listByPage(@PathVariable("pageNumber") int currentPage,
            @PathVariable("pageNumberUP") int currentPageUP, @Param("keyword") String keyword,
            Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
            String user = userDetails.getUsername();
            AccountEntity u = accountService.findUserByUserName(user);
            model.addAttribute("userDetails", u);
        }
        List<Movie> movies = movieService.listCurrent();
        Page<Movie> page = movieService.listAll(currentPage, keyword);
        long totalItems = page.getTotalElements();
        int totalPage = page.getTotalPages();
        List<Movie> list = page.getContent();
        Page<Movie> pageUpComming = movieService.listAllUpComming(currentPageUP, keyword);
        long totalItemsUpComming = pageUpComming.getTotalElements();
        int totalPageUpComming = pageUpComming.getTotalPages();
        List<Movie> listUpComming = pageUpComming.getContent();
        model.addAttribute("movies", movies);
        model.addAttribute("list", list);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("listUpComming", listUpComming);
        model.addAttribute("totalItemsUpComming", totalItemsUpComming);
        model.addAttribute("totalPageUpComming", totalPageUpComming);
        model.addAttribute("currentPageUP", currentPageUP);
        model.addAttribute("keyword", keyword);
        return "index";
    }

}
