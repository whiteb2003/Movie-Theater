package com.group5.cpl.controller;

import com.group5.cpl.exception.UserException;
import com.group5.cpl.service.EmployeeService;
import com.group5.cpl.utils.Account_Util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.repository.AccountRepository;
import com.group5.cpl.service.AccountService;

@Controller
public class AccountManagerController {
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    Account_Util account_util;

    @GetMapping({ "/account/manage", "/account/manage/all" })
    public String showAllAccount(Model model, Authentication authentication) {
        String username = null;
        String email = null;
        String phone = null;
        return listByPageAllAccount(1, username, email, phone, model, authentication);
    }

    @GetMapping("/account/manage/all/page/{pageNumber}")
    public String listByPageAllAccount(@PathVariable("pageNumber") int currentPage, @Param("username") String username,
            @Param("email") String email, @Param("phone") String phone,
            Model model, Authentication authentication) {
        Page<AccountEntity> page = accountService.listAllAccount(currentPage, username, email, phone);
        long totalItems = page.getTotalElements();
        int totalPage = page.getTotalPages();
        List<AccountEntity> list = page.getContent();
        model.addAttribute("list", list);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("key", "all");
        model.addAttribute("isManager", account_util.isUserManager(authentication));
        return "account_manage";
    }

    @GetMapping("/account/manage/active")
    public String showAllAccountActive(Model model, Authentication authentication) {
        String username = null;
        String email = null;
        String phone = null;
        return listByPageAllAccountActive(1, username, email, phone, model, authentication);
    }

    @GetMapping("/account/manage/active/page/{pageNumber}")
    public String listByPageAllAccountActive(@PathVariable("pageNumber") int currentPage,
            @Param("username") String username,
            @Param("email") String email, @Param("phone") String phone,
            Model model, Authentication authentication) {
        Page<AccountEntity> page = accountService.listAllAccountActive(currentPage, username, email, phone);
        long totalItems = page.getTotalElements();
        int totalPage = page.getTotalPages();
        List<AccountEntity> list = page.getContent();
        model.addAttribute("list", list);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("key", "active");
        model.addAttribute("isManager", account_util.isUserManager(authentication));
        return "account_manage";
    }

    @GetMapping("/account/manage/ban")
    public String showAllAccountHidden(Model model, Authentication authentication) {
        String username = null;
        String email = null;
        String phone = null;
        return listByPageAllAccountBan(1, username, email, phone, model, authentication);
    }

    @GetMapping("/account/manage/ban/page/{pageNumber}")
    public String listByPageAllAccountBan(@PathVariable("pageNumber") int currentPage,
            @Param("username") String username,
            @Param("email") String email, @Param("phone") String phone,
            Model model, Authentication authentication) {
        Page<AccountEntity> page = accountService.listAllAccountBan(currentPage, username, email, phone);
        long totalItems = page.getTotalElements();
        int totalPage = page.getTotalPages();
        List<AccountEntity> list = page.getContent();
        model.addAttribute("list", list);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("key", "ban");
        model.addAttribute("isManager", account_util.isUserManager(authentication));
        return "account_manage";
    }

    @GetMapping("account/manage/{key}/{id}")
    public String setUpdate(@PathVariable("id") Long id, @PathVariable("key") String key,
            RedirectAttributes redirectAttributes) {
        AccountEntity acc = accountService.findUserById(id);
        if (acc.getRoleEntity().getName().equals("MANAGER")) {
            redirectAttributes.addFlashAttribute("error", "You can not edit!");
            return "redirect:/account/manage/" + key;

        }
        accountRepository.setActive(!acc.isStatus(), id);
        return "redirect:/account/manage/" + key;
    }

    @GetMapping("employee/manage")
    public String showEmployeeManager(Model model, Authentication authentication) {
        model.addAttribute("list", accountRepository.listAllExceptEmployee());
        model.addAttribute("isManager", account_util.isUserManager(authentication));
        return "employee";
    }

    @GetMapping("employee/manage/remove/{id}")
    public String removeEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        AccountEntity acc = accountService.findUserById(id);
        if (acc.getRoleEntity().getName().equals("MANAGER")) {
            redirectAttributes.addFlashAttribute("error", "You can not remove employee!");
            return "redirect:/employee/manage";
        }
        accountRepository.removeEmployee(id);
        return "redirect:/employee/manage";
    }

    @GetMapping("employee/manage/add/{id}")
    public String AddEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        AccountEntity acc = accountService.findUserById(id);
        if (acc.getRoleEntity().getName().equals("MANAGER")) {
            redirectAttributes.addFlashAttribute("error", "You can not add employee!");
            return "redirect:/employee/manage";
        }
        accountRepository.addEmployee(id);
        return "redirect:/employee/manage";
    }

    @PostMapping("/employee/manage/search")
    public String employeeSearch(Authentication authentication, Model model,
            @RequestParam(name = "user_name", required = false) String user_name,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber) {
        model.addAttribute("isManager", account_util.isUserManager(authentication));
        List<AccountEntity> list = accountService.findEmployee(user_name, email, phoneNumber);
        model.addAttribute("list", list);
        model.addAttribute("user_name", user_name);
        model.addAttribute("email", email);
        model.addAttribute("phoneNumber", phoneNumber);
        return "employee";
    }

    @PostMapping("/employee/manage/search-add")
    public ResponseEntity<?> searchEmailEmployee(@RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
        try {
            employeeService.accountByEmail(email);
            return ResponseEntity.ok("Thành công");
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
