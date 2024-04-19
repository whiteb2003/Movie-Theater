package com.group5.cpl.service.serviceImp;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.group5.cpl.model.dto.ChangePasswordDTO;
import com.group5.cpl.model.dto.ProfileAccountDto;
import com.group5.cpl.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group5.cpl.exception.UserException;
import com.group5.cpl.model.AccountEntity;
import com.group5.cpl.model.RoleEntity;
import com.group5.cpl.model.dto.AccountDto;
import com.group5.cpl.repository.AccountRepository;
import com.group5.cpl.repository.AccountRepositoryPaging;
import com.group5.cpl.repository.RoleRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountServiceImp implements AccountService {
    @Autowired
    AccountRepository repo;
    @Autowired
    RoleRepository repo1;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AccountRepositoryPaging accountRepositoryPaging;

    public static String generateRandomString() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public AccountEntity findUserByUserName(String username) {
        Optional<AccountEntity> user_o = repo.findUserByUsernName1(username);
        if (!user_o.isPresent()) {
            return null;
        }
        AccountEntity u = user_o.get();
        return u;
    }

    @Override
    public AccountEntity findUserById(Long id) {
        Optional<AccountEntity> user_o = repo.findById(id);
        if (!user_o.isPresent()) {
            return null;
        }
        AccountEntity u = user_o.get();
        return u;
    }

    @Override
    public void saveAccount(AccountDto accountDto) {
        Optional<AccountEntity> findEmail = repo.findUserByEmail(accountDto.getEmail());
        Optional<AccountEntity> findUser = repo.findUserByUsernName1(accountDto.getUsername());
        Optional<AccountEntity> findPhone = repo.findUserByPhone(accountDto.getPhoneNumber());

        if (findUser.isPresent()) {
            throw new UserException("The username was exist");
        }

        if (findEmail.isPresent()) {
            throw new UserException("The email was exist");
        }

        if (findPhone.isPresent()) {
            throw new UserException("The phone number was exist");
        }

        if (!accountDto.getConfirmPassword().equals(accountDto.getPassword())) {
            throw new UserException("Check password again");
        }

        accountDto.setVerify_Code(generateRandomString());
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUsername(accountDto.getUsername());
        accountEntity.setFullname(accountDto.getFullname());
        accountEntity.setAddress(accountDto.getAddress());
        accountEntity.setDob(accountDto.getDob());
        accountEntity.setGender(accountDto.getGender());
        accountEntity.setEmail(accountDto.getEmail());
        accountEntity.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        accountEntity.setStatus(false);
        accountEntity.setVerifyCode(accountDto.getVerify_Code());
        accountEntity.setScore(0);
        accountEntity.setPhoneNumber(accountDto.getPhoneNumber());
        accountEntity.setRegisterDate(LocalDate.now());
        RoleEntity role = repo1.findByName("USER");
        accountEntity.setRoleEntity(role);
        repo.save(accountEntity);
    }

    @Override
    public void sendEmail(String email, String mailcontent) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("haihdhe170135@fpt.edu.vn", "megftjteypnebxdr");
            }
        });
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("haihdhe170135@fpt.edu.vn", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("Please verify your registration", "utf-8");
        msg.setContent(mailcontent, "text/html;charset=utf-8");
        Transport.send(msg);
    }

    @Override
    public void sendQR(String email, String path, String code, String movie, Date date, String time)
            throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("haihdhe170135@fpt.edu.vn", "megftjteypnebxdr");
            }
        });
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("haihdhe170135@fpt.edu.vn", false));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        msg.setSubject("Please verify your registration", "utf-8");
        MimeMultipart multipart = new MimeMultipart("related");
        BodyPart messageBodyPart = new MimeBodyPart();
        String htmlText = "<h2>You have booked " + movie + " in " + date + " at " + time + "</h3>";
        htmlText += "<h2>We send you code " + code + " and QR Code </h2> <img src=\"cid:image\">";
        htmlText += "<h3>This is automatic email! Please do not reply this email!</h3>";
        messageBodyPart.setContent(htmlText, "text/html;charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();
        DataSource fds = new FileDataSource(path);
        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setHeader("Content-ID", "<image>");
        multipart.addBodyPart(messageBodyPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }

    @Override
    public boolean verify(String verifyCode) {
        AccountEntity user = repo.findByVerificationCode(verifyCode);
        if (user == null || user.isStatus()) {
            return false;
        } else {
            repo.enable(user.getAccount_id());
            return true;
        }
    }

    @Override
    public Optional<AccountEntity> verifyEmail(String email) {
        Optional<AccountEntity> user_o = repo.findUserByEmail(email);

        return user_o;
    }

    @Override

    public void resetPassword(AccountEntity acc, String password) {
        String encodePass = passwordEncoder.encode(password);
        acc.setPassword(encodePass);
        acc.setVerifyCode(null);
        repo.save(acc);
    }
    // public void updatePassword(AccountEntity acc, String password){
    // String encodePass = passwordEncoder.encode(password);
    // acc.setPassword(encodePass);
    // if(!acc.getPassword().equals()){
    //
    // }
    // }

    @Override
    public AccountEntity findUserByVerifyCode(String code) {
        AccountEntity acc = repo.findByVerificationCode(code);
        return acc;
    }

    @Override
    public void updateToken(String token, String email) {
        Optional<AccountEntity> user_o = repo.findUserByEmail(email);
        if (user_o.isPresent()) {
            AccountEntity acc = user_o.get();
            acc.setVerifyCode(token);
            repo.save(acc);
        } else {
            throw new UserException("Could not find user by email");
        }
    }

    @Override
    public void updateUser(ProfileAccountDto profileAccountDto) {
        AccountEntity accountEntity = repo.findUserById(profileAccountDto.getId());
        // accountEntity.setUsername(profileAccountDto.getUsername());
        accountEntity.setFullname(profileAccountDto.getFullname());
        accountEntity.setDob(profileAccountDto.getDob());
        accountEntity.setGender(profileAccountDto.getGender());
        accountEntity.setAddress(profileAccountDto.getAddress());
        accountEntity.setPhoneNumber(profileAccountDto.getPhoneNumber());
        accountEntity.setImage(profileAccountDto.getImage());
        repo.saveAndFlush(accountEntity);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        AccountEntity accountEntity = repo.findUserById(changePasswordDTO.getUserId());
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), accountEntity.getPassword())) {
            throw new UserException("Current password entered incorrectly");
        }
        accountEntity.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        repo.saveAndFlush(accountEntity);
    }

    @Override
    public List<AccountEntity> listAll() {
        return repo.findAll();
    }

    @Override
    public Page<AccountEntity> listAllAccount(int pageNumber, String username, String email, String phone) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 4);
        if (username == null && email == null && phone == null) {
            return accountRepositoryPaging.listAll(pageable);
        }
        return accountRepositoryPaging.listAll(pageable, username, email, phone);
    }

    @Override
    public Page<AccountEntity> listAllAccountActive(int pageNumber, String username, String email, String phone) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 4);
        if (username == null && email == null && phone == null) {
            return accountRepositoryPaging.listAllActive(pageable);
        }
        return accountRepositoryPaging.listAllActive(pageable, username, email, phone);
    }

    @Override
    public Page<AccountEntity> listAllAccountBan(int pageNumber, String username, String email, String phone) {
        Pageable pageable = PageRequest.of(pageNumber - 1, 4);
        if (username == null && email == null && phone == null) {
            return accountRepositoryPaging.listAllBan(pageable);
        }
        return accountRepositoryPaging.listAllBan(pageable, username, email, phone);
    }

    // @Override
    // public AccountEntity searchForAddEmployee(String keyword) {
    // AccountEntity acc = repo.searchForAddEmployee(keyword);
    // return acc;
    // }

    @Override
    public List<AccountEntity> findEmployee(String username, String email, String phoneNumber) {
        List<AccountEntity> list = repo.findEmployee(username, email, phoneNumber);
        return list;
    }

}
