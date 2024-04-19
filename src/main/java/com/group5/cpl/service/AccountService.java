package com.group5.cpl.service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.data.domain.Page;

import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.dto.AccountDto;
import com.group5.cpl.model.dto.ChangePasswordDTO;
import com.group5.cpl.model.dto.ProfileAccountDto;

public interface AccountService {
    public AccountEntity findUserByUserName(String username);

    public AccountEntity findUserById(Long id);

    public void saveAccount(AccountDto accountDto);

    public void sendEmail(String email, String mailcontent) throws MessagingException;

    public void sendQR(String email, String path, String code, String movie, Date date, String time)
            throws MessagingException;

    public boolean verify(String verifyCode);

    public Optional<AccountEntity> verifyEmail(String email);

    public void resetPassword(AccountEntity acc, String password);

    public AccountEntity findUserByVerifyCode(String code);

    public void updateToken(String token, String email);

    public void updateUser(ProfileAccountDto accountEntity);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    List<AccountEntity> listAll();

    Page<AccountEntity> listAllAccount(int pageNumber, String username, String email, String phone);

    Page<AccountEntity> listAllAccountActive(int pageNumber, String username, String email, String phone);

    Page<AccountEntity> listAllAccountBan(int pageNumber, String username, String email, String phone);

    public List<AccountEntity> findEmployee(String username, String email, String phoneNumber);
}
