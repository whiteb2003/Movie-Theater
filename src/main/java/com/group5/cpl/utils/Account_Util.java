package com.group5.cpl.utils;

import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.MyUserDetail;

import com.group5.cpl.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class Account_Util {
    @Autowired private AccountService accountService;

    public boolean isUserManager(Authentication authentication) {
            MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();

//            String username = userDetails.getUsername();
//            AccountEntity user = accountService.findUserByUserName(username);

            // Get the authorities of the user
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            // Check if the user has the role of MANAGER
        boolean isManager = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("MANAGER"));

        return isManager;
    }

    public boolean isUserEmployee(Authentication authentication) {
        MyUserDetail userDetails = (MyUserDetail) authentication.getPrincipal();

//            String username = userDetails.getUsername();
//            AccountEntity user = accountService.findUserByUserName(username);

        // Get the authorities of the user
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        // Check if the user has the role of MANAGER
        boolean isEmployee = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("EMPLOYEE"));

        return isEmployee;
    }

    public boolean isAuthenticated(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            return true;
        }
        return false;
    }

    public MyUserDetail myUserDetails(Authentication authentication){
        return (MyUserDetail) authentication.getPrincipal();
    }

    public AccountEntity getAccountEntity(Authentication authentication){
        AccountEntity acc = accountService.findUserById(myUserDetails(authentication).getId());
        return acc;
    }
}
