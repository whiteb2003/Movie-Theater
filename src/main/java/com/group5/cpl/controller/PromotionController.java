package com.group5.cpl.controller;

import com.group5.cpl.model.Promotion;
import com.group5.cpl.model.enums.PromotionStatus;
import com.group5.cpl.repository.PromotionRepository;
import com.group5.cpl.service.AccountService;
import com.group5.cpl.service.PromotionService;
import com.group5.cpl.service.StorageService;
import com.group5.cpl.utils.Account_Util;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/promo")
public class PromotionController {
    private final PromotionService promotionService;
    private final AccountService accountService;
    private final Account_Util account_util;

    public PromotionController(PromotionService promotionService, AccountService accountService,
                               Account_Util account_util) {
        this.promotionService = promotionService;
        this.accountService = accountService;
        this.account_util = account_util;
    }

    @Autowired
    private StorageService storageService;
    @Autowired
    private PromotionRepository promotionRepository;

    // manager status only
    @GetMapping("/add")
    public String createNewPromotion(Model model, Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            if (isManager) {
                List<PromotionStatus> statusList = Arrays.asList(PromotionStatus.values());
                model.addAttribute("statusList", statusList);
                model.addAttribute("promotion", new Promotion());
                return "add_promo";
            } else {
                redirectAttributes.addFlashAttribute("error", "You must be a manager to create a promotion.");
                return "redirect:/home";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/home";

    }

    @PostMapping("/add")
    public String createPromotion(Model model,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  @ModelAttribute Promotion promotion,
                                  BindingResult bindingResult,
                                  @RequestParam("image") MultipartFile multipartFile) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            if (isManager) {
                // if (bindingResult.hasErrors()) {
                // // If there are validation errors, return to the form with error messages
                // return "add_promo";
                // }
                this.storageService.store(multipartFile);
                String fileImage = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                promotion.setImage(fileImage);
                promotionService.savePromotion(promotion);

                return "redirect:/promo/manage";
            } else {
                redirectAttributes.addFlashAttribute("error", "You must be a manager to create a promotion.");
                return "redirect:/home";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/home";
    }

    // @GetMapping("")

    @GetMapping("/manage")
    public String getAllPromotions(Model model,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes,
                                   @RequestParam(defaultValue = "0") int offset,
                                   @RequestParam(defaultValue = "5") int pageSize,
                                   HttpServletRequest request) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            String keyword = request.getParameter("keyword");
            boolean isManager = account_util.isUserManager(authentication);
            Pageable pageable = (Pageable) PageRequest.of(offset, pageSize, Sort.by("status"));
            Page<Promotion> promotionPage;
            if (isManager) {
                promotionPage = promotionService.getPromotions(keyword,pageable);
            } else {
                promotionPage = promotionService.getActivePromotions(pageable);
            }
            model.addAttribute("totalPromotions", promotionPage.getTotalElements());
            model.addAttribute("promotions", promotionPage.getContent());
            model.addAttribute("currentPage", promotionPage.getNumber());
            model.addAttribute("totalPages", promotionPage.getTotalPages());
            model.addAttribute("pageSize", promotionPage.getSize());
            model.addAttribute("isManager", isManager);
            return "promotion";
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";
    }

    @GetMapping("/available")
    public String getAvailablePromotions(Model model,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes,
                                         @RequestParam(defaultValue = "0") int offset,
                                         @RequestParam(defaultValue = "5") int pageSize) {
        if (account_util.isAuthenticated(authentication)) {
            boolean isManager = account_util.isUserManager(authentication);
            Pageable pageable = (Pageable) PageRequest.of(offset, pageSize);
            Page<Promotion> promotionPage = promotionService.getActivePromotions(pageable);
            model.addAttribute("promotions", promotionPage);
            model.addAttribute("totalPromotions", promotionPage.getTotalElements());
            model.addAttribute("currentPage", promotionPage.getNumber());
            model.addAttribute("totalPages", promotionPage.getTotalPages());
            model.addAttribute("pageSize", promotionPage.getSize());
            model.addAttribute("isManager", isManager);
            return "promotion"; // Show promotion list
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/login";
    }

    @GetMapping("/unavailable")
    public String getUnavailablePromotions(Model model,
                                           Authentication authentication,
                                           RedirectAttributes redirectAttributes,
                                           @RequestParam(defaultValue = "0") int offset,
                                           @RequestParam(defaultValue = "5") int pageSize) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            if (isManager) {
                Pageable pageable = (Pageable) PageRequest.of(offset, pageSize);
                Page<Promotion> promotionPage = promotionService.getUnactivePromotions(pageable);
                model.addAttribute("promotions", promotionPage);
                model.addAttribute("totalPromotions", promotionPage.getTotalElements());
                model.addAttribute("currentPage", promotionPage.getNumber());
                model.addAttribute("totalPages", promotionPage.getTotalPages());
                model.addAttribute("pageSize", promotionPage.getSize());
                model.addAttribute("isManager", isManager);
                return "promotion"; // Show promotion list
            } else {
                redirectAttributes.addFlashAttribute("error", "The page you are requesting is unavailable");
                return "redirect:/home";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/home";
    }

    @RequestMapping("/{promotionId}")
    public String getPromotionById(Model model,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes,
                                   @PathVariable("promotionId") Long promotionId) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            // either be a manager or promotion must be open
            Promotion promotion = promotionService.getPromotionByIDChecked(promotionId, isManager);
            if (promotion != null) {
                model.addAttribute("promotion", promotion);
                return "promotionInfo"; // Show promotion info
            } else {
                redirectAttributes.addFlashAttribute("error", "The page you are requesting is unvailable.");
                return "redirect:/promo/manage";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/home";
    }

    @GetMapping("/update/{promotionId}")
    public String updatePromotion(Model model,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  @PathVariable("promotionId") Long promotionId) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            if (isManager) {
                List<PromotionStatus> statusList = Arrays.asList(PromotionStatus.values());
                model.addAttribute("promotion", promotionService.getPromotionByID(promotionId));
                model.addAttribute("id", promotionId);
                model.addAttribute("statusList", statusList);
                return "edit_promo";
            } else {
                redirectAttributes.addFlashAttribute("error", "You must be a manager to create a promotion.");
                return "redirect:/home";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/home";
    }

    @PostMapping("/update/{promotionId}")
    public String updatePromotion(Model model,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes,
                                  @PathVariable("promotionId") Long promotionId,
                                  @ModelAttribute Promotion promotion,
                                  BindingResult bindingResult,
                                  @RequestParam("image") MultipartFile multipartFile,
                                  @RequestParam("status") String statusValue) {
        if (account_util.isAuthenticated(authentication)) {
            // Check if the user has the role of MANAGER
            boolean isManager = account_util.isUserManager(authentication);
            if (isManager) {
                this.storageService.store(multipartFile);
                String fileImage = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                promotion.setImage(fileImage);
                PromotionStatus status = PromotionStatus.valueOf(statusValue.toUpperCase());
                promotionService.updatePromotion(promotion, promotionId);
                promotionService.updatePromotionStatus(promotionId, status);
                return "redirect:/promo/manage";
            } else {
                redirectAttributes.addFlashAttribute("error", "You must be a manager to create a promotion.");
                return "redirect:/home";
            }
        }
        redirectAttributes.addFlashAttribute("error", "You must login to view this.");
        return "redirect:/home";
    }

    @GetMapping("/list")
    public String listPromo(Model model) {
        List<Promotion> p = promotionRepository.findAllActive();
        model.addAttribute("promotion", p);
        return "list_promo";
    }
}
