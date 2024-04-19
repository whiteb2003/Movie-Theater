
package com.group5.cpl.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.MyUserDetail;
import com.group5.cpl.repository.AccountRepository;
@Component
public class UserDetailServiceImp implements UserDetailsService{
    @Autowired
    AccountRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity user = repo.getUserByUserName(username);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserDetail(user);
    }
    
}
