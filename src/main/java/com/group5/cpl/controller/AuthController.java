package com.group5.cpl.controller;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import javax.mail.MessagingException;

import com.group5.cpl.model.MyUserDetail;
import com.group5.cpl.model.dto.ChangePasswordDTO;
import com.group5.cpl.model.dto.ProfileAccountDto;
import com.group5.cpl.service.AccountService;
import com.group5.cpl.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.group5.cpl.Utility.Utility;
import com.group5.cpl.exception.UserException;
import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.dto.AccountDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    public static String generateRandomString() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Autowired
    AccountService accountService;
    @Autowired
    private StorageService storageService;

    @GetMapping("/login")
    public String formLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new AccountDto());
        return "register";
    }

    @PostMapping("/process_register")
    public String register(@Valid @ModelAttribute("user") AccountDto user, BindingResult result,
            HttpServletRequest request, Model model) throws MessagingException {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$";
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        model.addAttribute("password", password);
        model.addAttribute("confirmPassword", confirmPassword);
        java.time.LocalDate now = java.time.LocalDate.now();
        if (result.hasErrors()) {
            return "register";
        }
        if (user.getDob() == null) {
            model.addAttribute("error", "Date of birth can not be null");
            return "register";
        } else if (user.getDob().toLocalDate().isAfter(now) || user.getDob().toLocalDate().isEqual(now)) {
            model.addAttribute("error", "Date of birth less than date now");
            return "register";
        }

        if (!password.matches(passwordRegex)) {
            model.addAttribute("errorPassword", "Error");
            return "register";
        } else if (!password.equals(confirmPassword)) {
            model.addAttribute("errorConfirmPassword", "Check password again");
            return "register";
        } else {
            user.setPassword(confirmPassword);
        }

        try {
            accountService.saveAccount(user);
            String mailcontent = "<h2> Dear " + user.getUsername() + ",</h2>";
            String siteURL = Utility.getSiteUrl(request);
            String verifyURL = siteURL + "/verify?code=" + user.getVerify_Code();
            mailcontent += "<p>You have created account in our system Cookie Cinema.  Please click link to activate account";
            mailcontent += "<h3><a href=\"" + verifyURL + "\">VERIFY</a></h3>";
            mailcontent += "<p>This is automatic email! Please do no reply this email!</p>";
            accountService.sendEmail(user.getEmail(), mailcontent);
            model.addAttribute("valueSendEmail", true);
            return "register";
        } catch (UserException ex) {
            switch (ex.getMessage()) {
                case "The username was exist":
                    result.addError(new FieldError("user", "username", "The username was exist"));
                    break;
                case "The email was exist":
                    result.addError(new FieldError("user", "email", "The email was exist"));
                    break;
                case "The phone number was exist":
                    result.addError(new FieldError("user", "phoneNumber", "The phone number was exist"));
                    break;
            }
            if (password.length() < 5 || password.length() > 20) {
                model.addAttribute("errorPassword", "Password length must be between 5 and 20 characters");
            }
            if (!password.equals(confirmPassword)) {
                model.addAttribute("errorConfirmPassword", "Check password again");
            }
            return "register";
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code) {
        boolean verified = accountService.verify(code);
        return verified ? "register_success" : "register_fail";
    }

    @GetMapping("/forgot_password")
    public String showForgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/existEmail")
    public String cofirmEmail(HttpServletRequest request, Model model) throws MessagingException {
        String email = request.getParameter("email");
        if (email.isEmpty()) {
            model.addAttribute("error", "Email can not be blank");
            return "forgot_password";
        }
        if (!email.contains("@")) {
            model.addAttribute("error", "Invalid email");
            return "forgot_password";
        }
        Optional<AccountEntity> user_o = accountService.verifyEmail(email);
        if (!user_o.isPresent()) {
            model.addAttribute("error", "Email is not exist");
            return "forgot_password";
        } else {
            AccountEntity acc = user_o.get();
            if (acc.isStatus()) {
                accountService.updateToken(generateRandomString(), email);
                String mailcontent = "<p> Dear " + acc.getUsername() + ",</p>";
                String siteURL = Utility.getSiteUrl(request);
                String verifyURL = siteURL + "/reset_password?code=" + acc.getVerifyCode();
                mailcontent += "<p>Please click link to reset your password";
                mailcontent += "<h3><a href=\"" + verifyURL + "\">VERIFY</a></h3>";
                mailcontent += "<p>This is automatic email! Please do no reply this email!</p>";
                accountService.sendEmail(email, mailcontent);
                model.addAttribute("valueSendEmail", true);
                return "forgot_password";
            }
            model.addAttribute("valueSendEmail", false);
            return "forgot_password";
        }
    }

    @GetMapping("/reset_password")
    public String showResetPassword(@Param("code") String code, Model model) {
        model.addAttribute("code", code);
        return "reset_password";
    }

    @PostMapping("/process_reset")
    public String reset(Model model, HttpServletRequest request) {
        String code = request.getParameter("code");
        String password = request.getParameter("password");
        AccountEntity acc = accountService.findUserByVerifyCode(code);
        if (acc == null) {
            model.addAttribute("accountNotExist", false);
            return "register_fail";
        } else if (!acc.isStatus()) {
            model.addAttribute("resetSuccessfull", false);
            return "register_fail";
        } else {
            accountService.resetPassword(acc, password);
            model.addAttribute("resetSuccessfull", true);
            return "reset_password";
        }
    }

    @GetMapping("/user-info/{id}")
    public String showUserInfo(@PathVariable("id") Long id, Model theModel) {
        AccountEntity theUser = accountService.findUserById(id);
        theModel.addAttribute("user", theUser);
        return "user-profile";
    }

    @PostMapping("/edit-profile/{id}")
    public String updateUser(@PathVariable("id") Long id,
            @RequestParam("imageUser") MultipartFile multipartFile,
            @ModelAttribute("user") ProfileAccountDto user,
            HttpServletRequest request,
            BindingResult result,
            Model model) {

        String fileUserName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        String file = request.getParameter("userfile");
        if (fileUserName.isEmpty()) {
            fileUserName = file;
        }
        user.setId(id);
        user.setImage(fileUserName);
        accountService.updateUser(user);
        this.storageService.store(multipartFile);
        return "redirect:/user-info/" + id;
    }

    @GetMapping("/change-password/{id}")
    public String showChangePassword(@PathVariable("id") Long id, Model theModel) {
        AccountEntity theUser = accountService.findUserById(id);
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setUserId(theUser.getAccount_id());
        theModel.addAttribute("changePasswordDTO", changePasswordDTO);
        theModel.addAttribute("user", theUser);
        return "change_password";
    }

    @PostMapping("/update-password")
    public String updatePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
            BindingResult result,
            Authentication authentication,
            HttpServletRequest request,
            Model theModel,
            RedirectAttributes redirectAttributes) throws MessagingException {
        Long user_id = null;
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$";
        if (authentication != null && authentication.isAuthenticated()) {
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();
            user_id = userDetails.getId();
            theModel.addAttribute("user", accountService.findUserById(user_id));
            if (result.hasErrors()) {
                return "change_password";
            }
            // New condition: Current password and new password must not be the same
            if (changePasswordDTO.getCurrentPassword().equals(changePasswordDTO.getNewPassword())) {
                result.addError(new FieldError("changePasswordDTO", "newPassword",
                        "New Password and Current Password can't be the same"));
                if (!changePasswordDTO.getNewPassword().matches(passwordRegex)) {
                    result.addError(new FieldError("changePasswordDTO", "newPassword",
                            "Password must have at least 8 characters and at least 1 special character"));
                }
                return "change_password";
            }
            // New condition: New password and Re-type new password must be the same
            if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRePassword())) {
                result.addError(new FieldError("changePasswordDTO", "rePassword",
                        "New Password and Re-Type New Password must be the same"));
                return "change_password";
            }
            try {
                changePasswordDTO.setUserId(user_id);
                accountService.changePassword(changePasswordDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Password successfully changed.");
            } catch (UserException ex) {
                switch (ex.getMessage()) {
                    case "Current password entered incorrectly":
                        result.addError(new FieldError("changePasswordDTO", "currentPassword",
                                "Current password entered incorrectly"));
                        return "change_password";
                }
            }

        }
        return "redirect:/change-password/" + user_id;
    }

    @GetMapping("/denied")
    public String denied() {
        return "denied";
    }
}
